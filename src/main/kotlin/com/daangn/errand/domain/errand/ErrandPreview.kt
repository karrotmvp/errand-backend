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
    var status: String?,
    val category: CategoryVo,
    var helpCount: Long,
    var regionName: String?
) {
    fun setStatus(errand: Errand, didUserApplyButWasNotChosen: Boolean) {
        val statusEnum = if (errand.complete) {
            Status.COMPLETE
        } else if (didUserApplyButWasNotChosen) {
            Status.FAIL
        } else if (errand.chosenHelper != null){
            Status.PROCEED
        } else {
            Status.WAIT
        }
        this.status = statusEnum.name
    }
}

enum class Status {
    WAIT,
    PROCEED,
    COMPLETE,
    FAIL;
}
