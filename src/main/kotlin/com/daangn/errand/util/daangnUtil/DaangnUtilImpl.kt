package com.daangn.errand.util.daangnUtil

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.daangn.*
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.fasterxml.jackson.databind.ObjectMapper
import datadog.trace.api.Trace
import io.sentry.spring.tracing.SentrySpan
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class DaangnUtilImpl(
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,
    @Value("\${daangn.open-api.url}") val openApiBaseUrl: String,
    @Value("\${daangn.oapi.url}") val oApiBaseUrl: String,
    @Value("\${daangn.app-auth}") val appAuthorization: String,
    @Value("\${daangn.app-key}") val appKey: String,
    @Value("\${daangn.oapi.neighbor-range}") val range: String,
): DaangnUtil {
    protected val SCOPE = "account/profile"
    protected val GRANT_TYPE = "authorization_code"
    protected val RESPONSE_TYPE = "code"

    override fun getAccessTokenByOpenApi(authCode: String): GetAccessTokenRes {
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
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        val responseBody: String? = httpResponse.body?.string()
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR, httpResponse.body.toString())
        }
        return try {
            objectMapper.readValue(responseBody, GetAccessTokenRes::class.java)
        } catch (e: Exception) {
            throw ErrandException(ErrandError.UNEXPECTED_ERROR, e.toString())
        }
    }

    override fun getMyProfile(accessToken: String): GetUserProfileRes.Data {
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
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR, "?????? ????????? ?????? ?????? ??????")
        }
        return try {
            objectMapper.readValue(httpResponse.body?.string(), GetUserProfileRes::class.java).data
        } catch (e: Exception) {
            throw ErrandException(ErrandError.UNEXPECTED_ERROR, e.toString())
        }
    }

    override fun getRegionInfoByRegionId(regionId: String): GetRegionInfoRes.Data {
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
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        val responseBody: String? = httpResponse.body?.string()
        if (!httpResponse.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR, "?????? ?????? ?????? ?????? ??????")
        }
        return try {
            objectMapper.readValue(responseBody, GetRegionInfoRes::class.java).data
        } catch (e: Exception) {
            throw ErrandException(ErrandError.UNEXPECTED_ERROR, e.toString())
        }

    }

    @SentrySpan
    override fun sendBizChatting(postBizChatReq: PostBizChatReq) {
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
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        if (!response.isSuccessful) {
            throw ErrandException(ErrandError.DAANGN_ERROR, "?????? ?????? ?????? ????????? ??????")
        }
    }

    @Trace
    override fun getNeighborRegionByRegionId(regionId: String): GetNeighborRegionInfoRes {
        val url = "$oApiBaseUrl/api/v2/regions/$regionId/neighbor_regions"
        val httpUrl = url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("range", range).build() // MY, ADJACENT, RANGE_2, RANGE_3
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("X-Api-Key", appKey)
            .build()
        val httpResponse = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        val responseBody: String? = httpResponse.body?.string()
        return try {
            objectMapper.readValue(responseBody, GetNeighborRegionInfoRes::class.java)
        } catch (e: Exception) {
            throw ErrandException(ErrandError.UNEXPECTED_ERROR, e.toString())
        }
    }


    override fun setUserDaangnProfile(user: UserProfileVo, regionId: String?): UserProfileVo {
        val getUserInfoRes = getUserProfile(user.daangnId)
        val userInfoRes = getUserInfoRes.data.user
        user.nickname = userInfoRes.nickname
        user.profileImageUrl = userInfoRes.profileImageUrl
        user.mannerTemp = userInfoRes.mannerTemperature
        if (regionId != null) user.regionName = getRegionInfoByRegionId(regionId).region.name
        return user
    }

    override fun getUserProfile(daangnId: String): GetUserInfoByUserIdRes {
        val url = "$oApiBaseUrl/api/v2/users/$daangnId"
        val httpUrl = url.toHttpUrlOrNull()!!.newBuilder().build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("X-Api-Key", appKey)
            .build()
        val httpResponse = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        val responseBody: String? = httpResponse.body?.string()
        return objectMapper.readValue(responseBody, GetUserInfoByUserIdRes::class.java)
    }

    override fun getUsersProfile(daangnIdList: List<String>): GetUserInfoByUserIdListRes {
        val userIds = daangnIdList.joinToString(separator = ",")
        val urlString = "$oApiBaseUrl/api/v2/users/by_ids"
        val httpUrl = urlString.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("ids", userIds)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("X-Api-Key", appKey)
            .build()
        val httpResponse = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ErrandException(ErrandError.DAANGN_ERROR, e.toString())
        }
        val responseBody: String? = httpResponse.body?.string()
        return objectMapper.readValue(responseBody, GetUserInfoByUserIdListRes::class.java)
    }

    @Trace(operationName = "regionInfo Map")
    override fun getRegionInfoByRegionIdMap(regionIds: MutableSet<String>): MutableMap<String, String> {
        val urlString = "$oApiBaseUrl/api/v2/regions/by_ids"
        val regionIdSequence = regionIds.joinToString(",")
        val httpUrl = urlString.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("ids", regionIdSequence)
            .build()
        val request = Request.Builder().url(httpUrl).get().addHeader("X-Api-Key", appKey).build()
        val httpResponse = httpClient.newCall(request).execute()
        val responseBody: String? = httpResponse.body?.string()
        val res = objectMapper.readValue(responseBody, GetRegionInfoListRes::class.java)
        val ret: MutableMap<String, String> = HashMap()
        res.data.regions.forEach { r -> ret[r.id] = r.name }
        return ret
    }
}