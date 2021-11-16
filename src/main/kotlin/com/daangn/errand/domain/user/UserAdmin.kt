package com.daangn.errand.domain.user

data class UserAdmin(
    val id: Long,
    val daangnId: String,
    val nickname: String?,
    val profileImageUrl: String?,
    val mannerTemperature: Float?,
    val errandCount: Int = 0,
    val helpCount: Int = 0,
)
