package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryVo
import com.daangn.errand.domain.image.ImageVo
import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.daangn.RegionVo
import java.time.LocalDateTime

class ErrandAdmin(
    val id: Long?,
    val customer: UserProfileVo,
    val category: CategoryVo,
    val reward: String,
    val detail: String,
    val customerPhoneNumber: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val images: List<ImageVo>,
    val viewCnt: Long,
    val complete: Boolean,
    val deleted: Boolean,
    val unexposed: Boolean,
    val chosenHelper: UserProfileVo?,
    val detailAddress: String? = null,
) {
    var helpCount: Long? = null
    var region: RegionVo? = null
}