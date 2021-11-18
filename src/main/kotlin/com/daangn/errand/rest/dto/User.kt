package com.daangn.errand.rest.dto

import io.swagger.annotations.ApiModelProperty


data class PatchUserAlarmReqDto(
    @ApiModelProperty(value = "알림 활성여부")
    val on: Boolean
)

data class GetUserAlarmResDto(
    val categoryStatusList: List<CategoryStatus>,
    val newHelpAlarm: Boolean
)

data class CategoryStatus(
    val categoryId: Long,
    val name: String,
    val status: Boolean
)
