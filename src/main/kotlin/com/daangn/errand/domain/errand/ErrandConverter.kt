package com.daangn.errand.domain.errand

import com.daangn.errand.domain.category.CategoryConverter
import com.daangn.errand.domain.help.HelpConverter
import com.daangn.errand.domain.image.ImageConverter
import com.daangn.errand.domain.user.UserConverter
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [CategoryConverter::class, UserConverter::class, ImageConverter::class])
interface ErrandConverter {
    @Mappings(
        Mapping(target = "region", ignore = true),
        Mapping(target = "isCompleted", source = "complete"),
        Mapping(target = "helpCount", ignore = true),
    )
    fun toErrandDto(errand: Errand): ErrandDto

    @Mappings(
        Mapping(target = "thumbnailUrl", ignore = true),
        Mapping(target = "status", ignore = true),
        Mapping(target = "helpCount", ignore = true),
        Mapping(target = "regionName", ignore = true)
    )
    fun toErrandPreview(errand: Errand): ErrandPreview

    @Mappings(
        Mapping(target = "helpCount", ignore = true),
        Mapping(target = "region", ignore = true)
    )
    fun toErrandAdmin(errand: Errand): ErrandAdmin

    @Mappings(
        Mapping(target = "thumbnailUrl", ignore = true),
        Mapping(target = "status", ignore = true),
        Mapping(target = "regionName", ignore = true)
    )
    fun toErrandPreview(mainErrandQueryResult: MainErrandQueryResult): ErrandPreview
}