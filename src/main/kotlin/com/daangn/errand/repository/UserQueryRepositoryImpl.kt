package com.daangn.errand.repository

import com.daangn.errand.domain.QHelperHasCategories.helperHasCategories
import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.user.QUser.user
import com.daangn.errand.domain.user.User
import com.querydsl.jpa.impl.JPAQueryFactory

class UserQueryRepositoryImpl(
    val query: JPAQueryFactory
) : UserQueryRepository {
    override fun findByDaangnIdListAndHasCategory(
        customerId: Long,
        daangnIds: MutableSet<String>,
        categoryId: Long
    ): MutableList<User> {
        return query.selectFrom(user)
            .join(helperHasCategories)
            .on(user.eq(helperHasCategories.user).and(helperHasCategories.category.id.eq(categoryId)))
            .where(user.daangnId.`in`(daangnIds).and(!user.id.eq(customerId)))
            .fetch()
    }
}