package com.daangn.errand.rest.dto.errand

import com.daangn.errand.domain.user.UserVo

data class LoginServiceResDto(
    val user: UserVo,
    val isSignUp: Boolean
)
