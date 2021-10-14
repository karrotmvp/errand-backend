package com.daangn.errand.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class JwtUtilTest constructor(
    @Autowired val jwtUtil: JwtUtil
) {

    @Test
    fun `userId랑 accessToken을 클레임으로 갖는 JWT 발행`() {
        val userId: Long = 1234567890
        val accessToken = "accessToken"
        val payload = JwtPayload(
            userId,
            accessToken
        )
        val jwt = jwtUtil.generateToken(payload)
        println(jwt)
        Assertions.assertThat(jwt).isNotNull
    }

    @Test
    fun `JWT 해독하기`() {
        val userId: Long = 12345678
        val jwt = jwtUtil.generateToken(
            JwtPayload(
                userId,
                "accessToken"
            )
        )
        val decodedPayload = jwtUtil.decodeToken(jwt)
        Assertions.assertThat(decodedPayload.userId).isEqualTo(userId)
        Assertions.assertThat(decodedPayload.accessToken).isEqualTo("accessToken")
    }
}