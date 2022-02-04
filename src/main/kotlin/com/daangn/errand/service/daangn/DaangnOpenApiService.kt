package com.daangn.errand.service.daangn

import com.daangn.errand.service.daangn.dto.GetAccessTokenRes
import com.daangn.errand.service.daangn.dto.PostBizChatReq

interface DaangnOpenApiService: DaangnRegionOpenApi, DaangnUserProfileOpenApi {
    fun getAccessTokenByOpenApi(authCode: String): GetAccessTokenRes
    fun sendBizChatting(postBizChatReq: PostBizChatReq)
    fun sendBizChatting(postBizChatReqs: List<PostBizChatReq>)
}
