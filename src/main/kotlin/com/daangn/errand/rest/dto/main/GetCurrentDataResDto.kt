package com.daangn.errand.rest.dto.main

data class GetCurrentDataResDto(
    val userCnt: Long,
    val matchedRate: Float,
    val userAlarmOnCnt: Long,
)
