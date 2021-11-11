package com.daangn.errand.repository

import com.daangn.errand.domain.image.Image

interface ImageQueryRepository {
    fun findOneByErrandIdOrderByIdDesc(errandId: Long): Image?

    fun findThumbnails(): MutableList<Image>
}