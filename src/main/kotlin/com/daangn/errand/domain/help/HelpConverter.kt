package com.daangn.errand.domain.help

import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.user.UserConverter
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [UserConverter::class])
interface HelpConverter {
    @Mappings(
        Mapping(target = "errandId", source = "errand.id")
    )
    fun toHelpVo(help: Help): HelpVo

    @Mappings(
        Mapping(target = "helper", ignore = true),
        Mapping(target = "region", ignore = true)
    )
    fun toHelpAdmin(help: Help): HelpAdmin
}