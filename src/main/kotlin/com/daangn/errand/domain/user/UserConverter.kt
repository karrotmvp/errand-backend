package com.daangn.errand.domain.user

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface UserConverter {
    @Mappings
    fun toUserVo(user: User): UserVo
}