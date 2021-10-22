package com.daangn.errand.domain.user

import io.swagger.annotations.ApiModelProperty

data class UserProfileVo(
    val id: Long? = null,
    val daangnId: String,
    var nickname: String? = null,
    var profileImageUrl: String? = null,
    var mannerTemp: Float? = null,
    var regionName: String? = null
)

data class UserVo(
    @ApiModelProperty(value = "심부름 서비스에서의 id")
    val id: Long? = null,
    @ApiModelProperty(value = "당근마켓 사용자 id")
    val daangnId: String
)
