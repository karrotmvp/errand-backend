package com.daangn.errand.domain.category

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface CategoryConverter {
    @Mappings
    fun toCategoryVo(category: Category): CategoryVo
}