package com.daangn.errand.domain.volunteer

import com.daangn.errand.domain.errand.ErrandVo
import com.daangn.errand.domain.user.UserVo

data class VolunteerVo(
    val id: Long?,
    val errand: ErrandVo,
    val helper: UserVo,
    val bio: String?
)
