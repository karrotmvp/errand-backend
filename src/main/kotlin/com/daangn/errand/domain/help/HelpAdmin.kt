package com.daangn.errand.domain.help

import com.daangn.errand.domain.errand.ErrandDto
import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.daangn.RegionVo
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class HelpAdmin(
    val id: Long,
    val errand: ErrandDto,
    val appeal: String,
    val phoneNumber: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    var region: RegionVo? = null
    var helper: UserProfileVo? = null
}