package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface ErrandRepository: JpaRepository<Errand, Long>, ErrandQueryRepository {
    fun countByCustomer(customer: User): Int
}