package com.daangn.errand.service.daangn.dto

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface RegionConverter {
    @Mappings
    fun toRegionVo(region: Region): RegionVo
}
