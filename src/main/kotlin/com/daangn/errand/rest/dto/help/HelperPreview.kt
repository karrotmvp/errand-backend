package com.daangn.errand.rest.dto.help

import com.daangn.errand.domain.user.UserProfileVo
import java.time.LocalDateTime

data class HelperPreview(
    val errandId: Long,
    val helpId: Long,
    val helper: UserProfileVo,
    val appeal: String,
    val createdAt: LocalDateTime
)
