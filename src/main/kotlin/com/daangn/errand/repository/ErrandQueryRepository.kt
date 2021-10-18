package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand

interface ErrandQueryRepository {
    fun findErrandsAfterLastErrandOrderByCreatedAtDesc(lastErrand: Errand, size: Long): MutableList<Errand>
    fun findErrandOrderByCreatedAtDesc(size: Long): MutableList<Errand>
}