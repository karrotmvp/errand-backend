package com.daangn.errand.domain.help

import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.user.UserConverter
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [UserConverter::class, ErrandConverter::class])
interface HelpConverter {
    @Mappings
    fun toHelpVo(help: Help): HelpVo
}