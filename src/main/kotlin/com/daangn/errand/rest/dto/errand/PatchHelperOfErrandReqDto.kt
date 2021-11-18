package com.daangn.errand.rest.dto.errand

import io.swagger.annotations.ApiModelProperty

data class PatchHelperOfErrandReqDto(
    @ApiModelProperty(value = "지정하려고 하는 헬퍼 아이디(유저 아이디)")
    val helperId: Long
)