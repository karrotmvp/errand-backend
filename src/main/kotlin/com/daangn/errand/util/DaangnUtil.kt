package com.daangn.errand.util

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.daangn.*
import com.daangn.errand.util.daangnUtil.DaangnRegionUtil
import com.daangn.errand.util.daangnUtil.DaangnUserProfileUtil

interface DaangnUtil: DaangnRegionUtil, DaangnUserProfileUtil {
    fun getAccessTokenByOpenApi(authCode: String): GetAccessTokenRes
    fun sendBizChatting(postBizChatReq: PostBizChatReq)
}