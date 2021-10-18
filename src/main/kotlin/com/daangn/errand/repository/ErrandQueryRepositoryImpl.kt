package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.QErrand.errand
import com.querydsl.jpa.impl.JPAQueryFactory

class ErrandQueryRepositoryImpl(
    val query: JPAQueryFactory
): ErrandQueryRepository {
    override fun findErrandsAfterLastErrandOrderByCreatedAtDesc(lastErrand: Errand, size: Long): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.createdAt.before(lastErrand.createdAt))
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandOrderByCreatedAtDesc(size: Long): MutableList<Errand> {
        return query.selectFrom(errand)
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }
}