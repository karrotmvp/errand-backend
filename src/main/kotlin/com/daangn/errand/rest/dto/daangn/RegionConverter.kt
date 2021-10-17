package com.daangn.errand.rest.dto.daangn

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface RegionConverter {
    @Mappings
    fun toRegionVo(region: Region): RegionVo
}