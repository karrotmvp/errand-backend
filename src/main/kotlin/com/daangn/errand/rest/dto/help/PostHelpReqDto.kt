package com.daangn.errand.rest.dto.help

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

data class PostHelpReqDto(
    @ApiModelProperty(value = "심부름 아이디")
    val errandId: Long,
    @ApiModelProperty(value = "지원자의 전화번호")
    val phoneNumber: String,
    @ApiModelProperty(value = "지원자의 각오")
    val appeal: String,
    @ApiModelProperty(value = "해당 카테고리 알림 설정 여부")
    val isSetAlarm: Boolean,
    @ApiModelProperty(value = "지원자의 지역 ID")
    val regionId: String
)