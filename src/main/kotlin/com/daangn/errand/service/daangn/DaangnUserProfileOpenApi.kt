package com.daangn.errand.service.daangn

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.service.daangn.dto.GetUserInfoByUserIdListRes
import com.daangn.errand.service.daangn.dto.GetUserInfoByUserIdRes
import com.daangn.errand.service.daangn.dto.GetUserProfileRes

interface DaangnUserProfileOpenApi {
    fun setUserDaangnProfile(user: UserProfileVo, regionId: String? = null): UserProfileVo
    fun getMyProfile(accessToken: String): GetUserProfileRes.Data
    fun getUserProfile(daangnAppId: String): GetUserInfoByUserIdRes
    fun getUsersProfile(daangnAppIdList: List<String>): GetUserInfoByUserIdListRes
}
