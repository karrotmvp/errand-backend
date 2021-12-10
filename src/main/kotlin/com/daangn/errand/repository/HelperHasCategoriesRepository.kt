package com.daangn.errand.repository

import com.daangn.errand.domain.HelperHasCategories
import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface HelperHasCategoriesRepository: JpaRepository<HelperHasCategories, Long> {
    fun findByUserAndCategory(user: User, category: Category): HelperHasCategories?
    fun findByCategory(category: Category): MutableList<HelperHasCategories>

    @Query(value = "SELECT COUNT(distinct h.user) FROM HelperHasCategories h")
    fun countAlarmOnUser(): Long
}