package com.daangn.errand.domain.user

import com.daangn.errand.domain.Value

data class UserVo(
    val id: Long?,
    val nickname: String,
    val phoneNumber: String,
    val profileImageUrl: String = Value.defaultImgUrl,
)
