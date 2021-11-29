package com.daangn.errand.domain.user

import java.time.LocalDateTime

data class UserPreview(
    val id: Long,
    val daangnId: String,
    val createdAt: LocalDateTime,
)
