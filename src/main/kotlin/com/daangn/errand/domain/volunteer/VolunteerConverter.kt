package com.daangn.errand.domain.volunteer

import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface VolunteerConverter {
    @Mappings
    fun toVolunteerVo(volunteer: Volunteer): VolunteerVo
}