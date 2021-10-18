package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryVo
import com.daangn.errand.domain.user.UserVo
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrandPreview(
    val id: Long,
    val title: String,
    val reward: String,
    var thumbnailUrl: String?,
    val chosenHelper: UserVo?,
    val status: String?,
    val category: CategoryVo
)

enum class Status {
    WAIT,
    PROCEED,
    COMPLETE,
    FAIL;

    override fun toString(): String {
        return super.name
    }
}
