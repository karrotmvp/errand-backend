package com.daangn.errand.repository

import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.image.QImage.image
import com.querydsl.jpa.impl.JPAQueryFactory
import javax.persistence.EntityManager
import javax.persistence.Query

class ImageQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val em: EntityManager
) : ImageQueryRepository {
    override fun findOneByErrandIdOrderByIdDesc(errandId: Long): Image? {
        return queryFactory.selectFrom(image)
            .where(image.errand.id.eq(errandId))
            .orderBy(image.id.desc())
            .fetchFirst()
    }

    override fun findThumbnails(): MutableList<Image> {
        val sql = """SELECT *
            FROM (SELECT * FROM IMAGE ORDER BY ID)
            GROUP BY errand_id
        """.trimIndent()
        val query: Query = em.createNativeQuery(sql, Image::class.java)
        return query.resultList as MutableList<Image>
    }
}