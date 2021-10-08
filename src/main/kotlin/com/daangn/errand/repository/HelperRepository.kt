package com.daangn.errand.repository

import com.daangn.errand.domain.Helper
import org.springframework.data.jpa.repository.JpaRepository

interface HelperRepository: JpaRepository<Helper, Long> {
}