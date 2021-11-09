package com.daangn.errand.rest.dto.help

import com.daangn.errand.domain.user.UserProfileVo

data class HelperPreview(
    val helpId: Long,
    val helper: UserProfileVo,
    val appeal: String,
)
