package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryVo
import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.rest.dto.daangn.RegionVo
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrandDto(
    val id: Long?,
    val customer: UserVo,
    var customerPhoneNumber: String?,
    val title: String,
    val category: CategoryVo,
    var detailAddress: String?,
    val reward: String,
    val detail: String,
    val isCompleted: Boolean,
    val chosenHelper: UserVo?
) {
    var region: RegionVo? = null
}