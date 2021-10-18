package com.daangn.errand.rest.dto.help

data class PostHelpReqDto(
    val helperId: Long,
    val errandId: Long,
    val phoneNumber: String,
    val appeal: String,
    val isSetAlarm: Boolean,
    val regionId: String
)