package com.daangn.errand.domain.image

import com.daangn.errand.domain.errand.ErrandConverter
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [ErrandConverter::class])
interface ImageConverter {
    @Mappings
    fun toImageVo(image: Image): ImageVo
}