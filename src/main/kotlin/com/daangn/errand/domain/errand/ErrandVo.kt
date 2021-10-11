package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryVo
import com.daangn.errand.domain.user.UserVo

data class ErrandVo(
    val id: Long,
    val customer: UserVo,
    val category: CategoryVo,
    val regionId: String,
    val detailAddress: String,
    val gratuity: String,
    val detail: String,
    val isCompleted: Boolean,
    val chosenHelper: UserVo
)