package com.daangn.errand.rest.dto.help

import com.daangn.errand.domain.user.UserProfileVo
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetHelpDetailResDto(
    val errandId: Long,
    val isCustomer: Boolean,
    val isMatched: Boolean, // TODO: isChosenHelper 로 바꾸기
    val helper: UserProfileVo,
    val appeal: String,
    val phoneNumber: String?,
    val createdAt: LocalDateTime
)