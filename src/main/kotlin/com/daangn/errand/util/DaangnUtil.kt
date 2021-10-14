package com.daangn.errand.util

import com.daangn.errand.rest.dto.daangn.*
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class DaangnUtil(
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,
    @Value("\${oauth.open-api}") val openApiBaseUrl: String,
    @Value("\${oauth.oapi}") val oApiBaseUrl: String,
    @Value("\${daangn.app-auth}") val appAuthorization: String,
    @Value("\${daangn.app-key}") val appKey: String
) {
    protected val SCOPE = "account/profile"
    protected val GRANT_TYPE = "authorization_code"
    protected val RESPONSE_TYPE = "code"

    fun getAccessTokenByOpenApi(authCode: String): GetAccessTokenRes {
        val url = "$openApiBaseUrl/oauth/token"
        val httpUrl = url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("code", authCode)
            .addQueryParameter("scope", SCOPE)
            .addQueryParameter("grant_type", GRANT_TYPE)
            .addQueryParameter("response_type", RESPONSE_TYPE).build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("Authorization", "Basic $appAuthorization")
            .build()

        val httpResponse = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setDescExceptionMsg(e))
        }
        val responseBody: String? = httpResponse.body?.string()
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setCustomDesc("당근 Access Token 획득 실패"))
        }
        return try {
            objectMapper.readValue(responseBody, GetAccessTokenRes::class.java)
        } catch (e: Exception) {
            throw ErrandException(ErrandError.CUSTOM_ERROR.setDescExceptionMsg(e))
        }
    }

    fun getUserInfo(accessToken: String): GetUserProfileRes.Data {
        val url = "$openApiBaseUrl/api/v1/users/me"
        val httpUrlBuilder = url.toHttpUrlOrNull()!!.newBuilder()
        val httpResponse = try {
            httpClient.newCall(
                Request.Builder()
                    .get()
                    .url(httpUrlBuilder.build())
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            ).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setDescExceptionMsg(e))
        }
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setCustomDesc("당근 프로필 정보 조회 실패"))
        }
        return try {
            objectMapper.readValue(httpResponse.body?.string(), GetUserProfileRes::class.java).data
        } catch (e: Exception) {
            throw ErrandException(ErrandError.CUSTOM_ERROR.setDescExceptionMsg(e))
        }
    }

    fun getMannerTemp(accessToken: String): GetUserMannerPointRes.Data {
        val url = "$openApiBaseUrl/api/v1/users/me/manner_point"
        val httpUrl = url.toHttpUrlOrNull()!!.newBuilder().build()
        val httpResponse = try {
            httpClient.newCall(
                Request.Builder()
                    .get()
                    .url(httpUrl)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            ).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.CUSTOM_ERROR.setDescExceptionMsg(e))
        }
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.CUSTOM_ERROR.setCustomDesc("당근 회원 매너온도 조회 실패"))
        }
        val res: GetUserMannerPointRes = try {
            objectMapper.readValue(httpResponse.body?.string(), GetUserMannerPointRes::class.java)
        } catch (e: Exception) {
            throw ErrandException(ErrandError.CUSTOM_ERROR.setDescExceptionMsg(e))
        }
        return res.data
    }

    fun getRegionInfoByRegionId(regionId: String): GetRegionInfoRes.Data {
        val url = "$oApiBaseUrl/api/v2/regions/$regionId"
        val httpUrl = url.toHttpUrlOrNull()!!.newBuilder().build()
        val httpResponse = try {
            httpClient.newCall(
                Request.Builder()
                    .url(httpUrl)
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("X-Api-Key", appKey)
                    .build()
            ).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setDescExceptionMsg(e))
        }
        val responseBody: String? = httpResponse.body?.string()
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setCustomDesc("당근 지역 정보 조회 실패"))
        }
        return try {
            objectMapper.readValue(responseBody, GetRegionInfoRes::class.java).data
        } catch (e: Exception) {
            throw ErrandException(ErrandError.CUSTOM_ERROR.setDescExceptionMsg(e))
        }
    }

    fun sendBizChatting(postBizChatReq: PostBizChatReq) {
        val url = "$oApiBaseUrl/api/v2/chat/send_biz_chat_message"
        val httpUrl = url.toHttpUrlOrNull()!!.newBuilder().build()

        val requestBody = objectMapper.writeValueAsString(postBizChatReq)
        val request: Request = Request.Builder()
            .url(httpUrl)
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("X-Api-Key", appKey)
            .build()
        val response = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setDescExceptionMsg(e))
        }
        if (!response.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR.setCustomDesc("당근 비즈 채팅 보내기 실패"))
        }
    }
}