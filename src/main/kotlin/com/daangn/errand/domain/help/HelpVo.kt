package com.daangn.errand.domain.help

import com.daangn.errand.domain.errand.ErrandDto
import com.daangn.errand.domain.user.UserVo

data class HelpVo(
    val id: Long?,
    val errand: ErrandDto,
    val helper: UserVo,
    val bio: String?
)
