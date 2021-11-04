package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryVo
import com.daangn.errand.domain.user.UserVo
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrandPreview(
    val id: Long,
    val detail: String,
    val reward: String,
    var thumbnailUrl: String?,
    val chosenHelper: UserVo?,
    override var status: String?,
    val category: CategoryVo,
    var helpCount: Long,
    var regionName: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
): ErrandHasStatus

enum class Status {
    WAIT,
    PROCEED,
    COMPLETE,
    FAIL;
}
