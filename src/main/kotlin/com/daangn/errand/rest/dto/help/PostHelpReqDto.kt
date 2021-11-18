package com.daangn.errand.rest.dto.help

import io.swagger.annotations.ApiModelProperty

data class PostHelpReqDto(
    @ApiModelProperty(value = "심부름 아이디")
    val errandId: Long,
    @ApiModelProperty(value = "지원자의 전화번호")
    val phoneNumber: String,
    @ApiModelProperty(value = "지원자의 각오")
    val appeal: String,
    @ApiModelProperty(value = "지원자의 지역 ID")
    val regionId: String
)