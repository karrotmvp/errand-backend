package com.daangn.errand.repository

import com.daangn.errand.domain.category.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository: JpaRepository<Category, Long> {
}