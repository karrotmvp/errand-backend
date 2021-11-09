package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.image.Image
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository: JpaRepository<Image, Long> {
    fun existsByErrand(errand: Errand): Boolean
}