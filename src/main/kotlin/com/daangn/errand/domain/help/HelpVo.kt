package com.daangn.errand.domain.help

import com.daangn.errand.domain.errand.ErrandDto
import com.daangn.errand.domain.user.UserVo

data class HelpVo(
    val id: Long?,
    val errandId: Long,
    val helper: UserVo,
    val appeal: String,
    val phoneNumber: String,
    val regionId: String
)
