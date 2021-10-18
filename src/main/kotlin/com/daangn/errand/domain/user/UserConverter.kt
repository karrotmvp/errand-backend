package com.daangn.errand.domain.user

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface UserConverter {
    @Mappings(
        Mapping(target = "nickname", ignore = true),
        Mapping(target = "profileImageUrl", ignore = true),
        Mapping(target = "mannerPoint", ignore = true),
        Mapping(target = "regionName", ignore = true)
    )
    fun toUserProfileVo(user: User): UserProfileVo

    @Mappings
    fun toUserVo(user: User): UserVo
}