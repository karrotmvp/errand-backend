package com.daangn.errand.domain.user

data class UserVo(
    val id: Long? = null,
    val nickname: String,
    val phoneNumber: String,
    val profileImageUrl: String? = null
)
