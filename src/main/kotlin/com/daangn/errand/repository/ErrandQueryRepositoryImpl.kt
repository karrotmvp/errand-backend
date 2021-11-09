package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.QErrand.errand
import com.daangn.errand.domain.help.QHelp.help
import com.daangn.errand.domain.user.User
import com.querydsl.core.types.dsl.Wildcard
import com.querydsl.core.types.dsl.Wildcard.count
import com.querydsl.jpa.impl.JPAQueryFactory

class ErrandQueryRepositoryImpl(
    val query: JPAQueryFactory
) : ErrandQueryRepository {
    override fun findErrandsAfterLastErrandOrderByCreatedAtDesc(
        lastErrand: Errand,
        size: Long,
        regionIds: List<String>
    ): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.regionId.`in`(regionIds).and(errand.unexposed.isFalse))
            .where(errand.createdAt.before(lastErrand.createdAt))
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandOrderByCreatedAtDesc(size: Long, regionIds: List<String>): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.regionId.`in`(regionIds).and(errand.unexposed.isFalse))
            .orderBy(errand.createdAt.desc())
            .orderBy(errand.id.desc())
            .limit(size)
            .fetch()
    }

    override fun findByCustomerOrderByCreateAtDesc(customer: User, size: Long): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.customer.eq(customer).and(errand.unexposed.isFalse))
            .orderBy(errand.createdAt.desc())
            .orderBy(errand.id.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandsAfterLastErrandByCustomerOrderedByCreatedAtDesc(
        lastErrand: Errand,
        customer: User,
        size: Long
    ): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.customer.eq(customer).and(errand.unexposed.isFalse))
            .where(errand.createdAt.before(lastErrand.createdAt))
            .orderBy(errand.createdAt.desc())
            .orderBy(errand.id.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandsEnableToApply(size: Long, regionIds: List<String>): MutableList<Errand> {
        return query.selectFrom(errand)
            .leftJoin(errand.helps, help).distinct()
            .where(
                errand.regionId.`in`(regionIds)
                    .and(errand.chosenHelper.isNull)
                    .and(errand.helps.size().lt(5))
                    .and(errand.unexposed.isFalse)
            )
            .fetchJoin()
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }

    override fun findErrandsEnableToApplyAfterLastErrand(
        lastErrand: Errand,
        size: Long,
        regionIds: List<String>
    ): MutableList<Errand> {
        return query.selectFrom(errand)
            .leftJoin(errand.helps, help).distinct()
            .where(
                errand.regionId.`in`(regionIds)
                    .and(errand.chosenHelper.isNull)
                    .and(errand.helps.size().lt(5))
                    .and(errand.createdAt.before(lastErrand.createdAt))
                    .and(errand.unexposed.isFalse)
            )
            .fetchJoin()
            .orderBy(errand.createdAt.desc())
            .limit(size)
            .fetch()
    }
}