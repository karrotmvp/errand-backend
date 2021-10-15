package com.daangn.errand.domain.user

import io.swagger.annotations.ApiModelProperty

data class UserProfileVo(
    @ApiModelProperty(value = "심부름 서비스에서의 id")
    val id: Long? = null,
    @ApiModelProperty(value = "당근마켓 사용자 id")
    val daangnId: String,
    @ApiModelProperty(value = "당근마켓 닉네임")
    val nickname: String? = null,
    @ApiModelProperty(value = "당근마켓 프로필사진")
    val profileImageUrl: String? = null,
    @ApiModelProperty(value = "당근마켓 매너온도")
    val mannerPoint: Float? = null
)

data class UserVo(
    @ApiModelProperty(value = "심부름 서비스에서의 id")
    val id: Long? = null,
    @ApiModelProperty(value = "당근마켓 사용자 id")
    val daangnId: String
)
