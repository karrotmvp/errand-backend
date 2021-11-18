package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.awt.print.Pageable

interface ErrandRepository: JpaRepository<Errand, Long>, ErrandQueryRepository {
    fun countByCustomer(customer: User): Int
}