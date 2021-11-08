package com.daangn.errand.rest.dto.help

import com.daangn.errand.domain.user.UserProfileVo

data class HelperWithHelpId(
    val helpId: Long,
    val helper: UserProfileVo
)
