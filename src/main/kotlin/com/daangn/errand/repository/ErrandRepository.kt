package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import org.springframework.data.jpa.repository.JpaRepository

interface ErrandRepository: JpaRepository<Errand, Long>, ErrandQueryRepository {
}