package com.daangn.errand.domain.errand

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface ErrandConverter {
    @Mappings(
        Mapping(target="region", ignore = true) // 여기서
    )
    fun toErrandDto(errand: Errand): ErrandDto
}