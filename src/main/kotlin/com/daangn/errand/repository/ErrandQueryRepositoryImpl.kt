package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.QErrand.errand
import com.daangn.errand.domain.user.User
import com.querydsl.jpa.impl.JPAQueryFactory

class ErrandQueryRepositoryImpl(
    val query: JPAQueryFactory
): ErrandQueryRepository {
    override fun findErrandsAfterLastErrandOrderByCreatedAtDesc(lastErrand: Errand, size: Long, regionIds: List<String>): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.regionId.`in`(regionIds))
            .where(errand.createdAt.before(lastErrand.createdAt))
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandOrderByCreatedAtDesc(size: Long, regionIds: List<String>): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.regionId.`in`(regionIds))
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }

    override fun findByCustomerOrderByCreateAtDesc(customer: User, size: Long): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.customer.eq(customer))
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandsAfterLastErrandByCustomerOrderedByCreatedAtDesc(
        lastErrand: Errand,
        customer: User,
        size: Long
    ): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.customer.eq(customer))
            .where(errand.createdAt.before(lastErrand.createdAt))
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }
}