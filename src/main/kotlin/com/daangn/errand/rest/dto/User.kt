package com.daangn.errand.rest.dto


data class patchUserAlarmReqDto(
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
