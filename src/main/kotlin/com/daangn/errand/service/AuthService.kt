package com.daangn.errand.service

import com.daangn.errand.service.daangn.DaangnOpenApiService
import com.daangn.errand.service.daangn.dto.GetUserProfileRes
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    val daangnOpenAPIService: DaangnOpenApiService
) {
    fun getAccessToken(authCode: String): String {
        return daangnOpenAPIService.getAccessTokenByOpenApi(authCode).accessToken
    }

    fun getUserProfile(accessToken: String): GetUserProfileRes.Data {
        return daangnOpenAPIService.getMyProfile(accessToken)
    }
}
