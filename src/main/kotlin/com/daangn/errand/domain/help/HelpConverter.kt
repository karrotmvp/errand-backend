package com.daangn.errand.domain.help

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface HelpConverter {
    @Mappings
    fun toHelpVo(help: Help): HelpVo
}