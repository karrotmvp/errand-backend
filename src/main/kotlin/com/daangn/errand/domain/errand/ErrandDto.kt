package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryVo
import com.daangn.errand.domain.image.ImageVo
import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.rest.dto.daangn.RegionVo
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrandDto(
    val id: Long?,
    val customer: UserVo,
    var customerPhoneNumber: String?,
    var images: List<ImageVo>,
    val category: CategoryVo,
    var detailAddress: String?,
    val reward: String,
    val detail: String,
    val isCompleted: Boolean = false,
    val chosenHelper: UserVo?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
): ErrandHasStatus {
    override var status: String? = null
    var region: RegionVo? = null
    var helpCount: Long? = null
}