package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.Category
import java.time.LocalDateTime

data class MainErrandQueryResult(
    val id: Long,
    val reward: String,
    val category: Category,
    val customerId: Long,
    val detail: String,
    val regionId: String,
    val complete: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    var chosenHelperId: Long? = null,
    var viewerHelpId: Long? = null
) {
    var helpCount: Long = 0
    var thumbnailUrl: String? = null
    override fun toString(): String {
        return "MainErrandQueryResult(customerId=$customerId, thumbnailUrl='$thumbnailUrl', detail='$detail', regionId='$regionId', chosenHelperId=$chosenHelperId, viewerHelpId=$viewerHelpId)"
    }
}
