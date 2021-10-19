package com.daangn.errand.repository

import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.help.QHelp.help
import com.daangn.errand.domain.user.User
import com.querydsl.jpa.impl.JPAQueryFactory

class HelpQueryRepositoryImpl (
    val query: JPAQueryFactory
        ): HelpQueryRepository {
    override fun findByHelperTopSize(helper: User, size: Long): MutableList<Help> {
        return query.selectFrom(help)
            .where(help.helper.eq(helper))
            .orderBy(help.createdAt.desc())
            .limit(size)
            .fetch()
    }
    override fun findByHelper(helper: User, lastHelp: Help, size: Long): MutableList<Help> {
        return query.selectFrom(help)
            .where(help.helper.eq(helper))
            .where(help.createdAt.before(lastHelp.createdAt))
            .orderBy(help.createdAt.desc())
            .limit(size)
            .fetch()
    }
}