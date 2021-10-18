package com.daangn.errand.service

import com.daangn.errand.rest.dto.daangn.GetUserProfileRes
import com.daangn.errand.util.DaangnUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    val daangnUtil: DaangnUtil
) {
    fun getAccessToken(authCode: String): String {
        return daangnUtil.getAccessTokenByOpenApi(authCode).accessToken
    }
    fun getUserProfile(accessToken: String): GetUserProfileRes.Data {
        return daangnUtil.getUserInfo(accessToken)
    }
}