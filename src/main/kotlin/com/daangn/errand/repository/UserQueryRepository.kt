package com.daangn.errand.repository

import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.user.User

interface UserQueryRepository {
    fun findByDaangnIdListAndHasCategory(customerId: Long, daangnIds: MutableSet<String>, categoryId: Long): MutableList<User>
}