package com.daangn.errand.rest.dto.help

import com.daangn.errand.domain.user.UserProfileVo

data class GetHelpDetailResDto(
    val isMatched: Boolean,
    val helper: UserProfileVo,
    val appeal: String,
    val phoneNumber: String
)