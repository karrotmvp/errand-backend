package com.daangn.errand.domain.image

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface ImageConverter {
    @Mappings
    fun toImageVo(image: Image): ImageVo

    @Mappings
    fun toImageVoList(images: MutableList<Image>): List<ImageVo>
}