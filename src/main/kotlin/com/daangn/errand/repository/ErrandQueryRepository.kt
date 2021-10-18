package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User

interface ErrandQueryRepository {
    fun findErrandsAfterLastErrandOrderByCreatedAtDesc(lastErrand: Errand, size: Long): MutableList<Errand>
    fun findErrandOrderByCreatedAtDesc(size: Long): MutableList<Errand>
    fun findByCustomerOrderByCreateAtDesc(customer: User, size: Long): MutableList<Errand>
    fun findErrandsAfterLastErrandByCustomerOrderedByCreatedAtDesc(
        lastErrand: Errand,
        customer: User,
        size: Long
    ): MutableList<Errand>
}