package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.MainErrandQueryResult
import com.daangn.errand.domain.errand.QErrand.errand
import com.daangn.errand.domain.help.QHelp.help
import com.daangn.errand.domain.user.User
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import datadog.trace.api.Trace

class ErrandQueryRepositoryImpl(
    val query: JPAQueryFactory
) : ErrandQueryRepository {


    override fun findByCustomerOrderByCreateAtDesc(customer: User, size: Long): MutableList<Errand> {
        return query.selectFrom(errand)
            .where(errand.customer.eq(customer).and(errand.unexposed.isFalse))
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
            .where(errand.id.lt(lastErrand.id))
            .orderBy(errand.id.desc())
            .groupBy(errand.id)
            .limit(size)
            .fetch()
    }

    override fun findAppliableMainErrands(
        lastErrandId: Long?,
        viewerId: Long,
        regionIds: List<String>,
        size: Long
    ): MutableList<MainErrandQueryResult> {
        val predicate = BooleanBuilder()
        predicate.and(errand.unexposed.isFalse).and(errand.complete.isFalse).and(!errand.customer.id.eq(viewerId))
            .and(errand.chosenHelper.isNull)
            .and(errand.helps.size().lt(5))
            .and(errand.regionId.`in`(regionIds))
        if (lastErrandId != null) predicate.and(errand.id.lt(lastErrandId))
        val errandIdsAlreadyAppliedByUser: List<Long> =
            query.select(errand.id).from(help).where(help.helper.id.eq(viewerId)).fetch()
        predicate.and(errand.id.notIn(errandIdsAlreadyAppliedByUser))


        return query.select(
            Projections.constructor(
                MainErrandQueryResult::class.java,
                errand.id,
                errand.reward,
                errand.category,
                errand.customer.id.`as`("customerId"),
                errand.detail,
                errand.regionId,
                errand.complete,
                errand.count().longValue().`as`("helpCount"),
                errand.createdAt,
                errand.updatedAt,
                errand.chosenHelper.id.`as`("chosenHelperId"),
                help.id.`as`("reviewerHelpId")
            )
        ).from(errand)
            .leftJoin(help).on(help.errand.id.eq(errand.id))
            .orderBy(errand.id.desc())
            .groupBy(errand.id)
            .where(predicate)
            .limit(size)
            .fetch()
    }

    @Trace
    override fun findMainErrands(
        viewerId: Long,
        size: Long,
        regionIds: List<String>,
        lastErrandId: Long?
    ): MutableList<MainErrandQueryResult> {
        val predicate = BooleanBuilder()
        if (lastErrandId != null) predicate.and(errand.id.lt(lastErrandId))
        return query.select(
            Projections.constructor(
                MainErrandQueryResult::class.java,
                errand.id,
                errand.reward,
                errand.category,
                errand.customer.id.`as`("customerId"),
                errand.detail,
                errand.regionId,
                errand.complete,
                errand.count().longValue().`as`("helpCount"),
                errand.createdAt,
                errand.updatedAt,
                errand.chosenHelper.id.`as`("chosenHelperId"),
                help.id.`as`("reviewerHelpId"),
            )
        ).from(errand)
            .leftJoin(help).on(help.errand.id.eq(errand.id).and(help.helper.id.eq(viewerId)))
            .where(predicate)
            .where(errand.regionId.`in`(regionIds))
            .orderBy(errand.id.desc())
            .groupBy(errand.id)
            .limit(size).fetch()
    }
}