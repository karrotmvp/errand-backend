package com.daangn.errand.util.daangnUtil

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.daangn.GetUserInfoByUserIdListRes
import com.daangn.errand.rest.dto.daangn.GetUserInfoByUserIdRes
import com.daangn.errand.rest.dto.daangn.GetUserProfileRes

interface DaangnUserProfileUtil {
    fun setUserDaangnProfile(user: UserProfileVo, regionId: String? = null): UserProfileVo
    fun getMyProfile(accessToken: String): GetUserProfileRes.Data
    fun getUserProfile(daangnAppId: String): GetUserInfoByUserIdRes
    fun getUsersProfile(daangnAppIdList: List<String>): GetUserInfoByUserIdListRes
}