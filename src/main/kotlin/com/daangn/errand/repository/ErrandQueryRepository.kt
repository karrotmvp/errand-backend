package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.MainErrandQueryResult
import com.daangn.errand.domain.user.User

interface ErrandQueryRepository {
    fun findByCustomerOrderByCreateAtDesc(
        lastErrand: Errand?,
        customer: User,
        size: Long
    ): MutableList<Errand>

    fun findAppliableMainErrands(
        lastErrandId: Long?,
        viewerId: Long,
        regionIds: List<String>,
        size: Long
    ): MutableList<MainErrandQueryResult>

    fun findMainErrands(viewerId: Long, size: Long, regionIds: List<String>, lastErrandId: Long? = null): MutableList<MainErrandQueryResult>
}