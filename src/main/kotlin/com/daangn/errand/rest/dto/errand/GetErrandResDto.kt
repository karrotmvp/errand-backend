package com.daangn.errand.rest.dto.errand

import com.daangn.errand.domain.errand.ErrandDto
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetErrandResDto(
    var errand: ErrandDto,
    var isMine: Boolean,
    var didIApply: Boolean,
    var wasIChosen: Boolean,
)
