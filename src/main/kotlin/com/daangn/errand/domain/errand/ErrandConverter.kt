package com.daangn.errand.domain.errand

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface ErrandConverter {
    @Mappings
    fun toErrandVo(errand: Errand): ErrandVo
}