package com.daangn.errand.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Duration
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import kotlin.jvm.Throws

@Component
class JwtUtil (
    @Value("\${token.secret}")
    private val secretKey: String
        ) {
    @Throws(JsonProcessingException::class)
    fun generateToken(payload: JwtPayload): String {
        val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.HS256
        val secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKey) // convert string to byte
        val signingKey: Key = SecretKeySpec(secretKeyBytes, signatureAlgorithm.jcaName)

        val now = Date()
        return Jwts.builder()
            .claim("userId", payload.userId)
            .claim("accessToken", payload.accessToken)
            .setExpiration(Date(now.time + Duration.ofDays(1).toMillis()))
            .signWith(signatureAlgorithm, signingKey)
            .compact()
    }

    @Throws(JsonProcessingException::class)
    fun decodeToken(token: String): JwtPayload {
        val claims: Claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
            .parseClaimsJws(token)
            .body
        return JwtPayload(
            claims.get("userId", Integer::class.java).toLong(), //userId가 integer로 인식돼서 바로 Long으로 가져오면 에러.
            claims.get("accessToken", String::class.java)
        )
    }
}

data class JwtPayload (
    val userId: Long,
    val accessToken: String
        )