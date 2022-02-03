package com.daangn.errand.repository

import com.daangn.errand.domain.user.User

interface UserQueryRepository {
    fun findUsersInDaangnIdsAndHavingCategory(daangnIds: MutableSet<String>, categoryId: Long): MutableList<User>
}
