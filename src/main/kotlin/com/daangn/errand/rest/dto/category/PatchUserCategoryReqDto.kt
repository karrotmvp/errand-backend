package com.daangn.errand.rest.dto.category

import io.swagger.annotations.ApiModelProperty

data class PatchUserCategoryReqDto(
    @ApiModelProperty(value = "알림 설정할 카테고리 ID")
    val categoryId: Long,
    @ApiModelProperty(value = "알림을 설정하는지 끄는 건지 알려주는 필드")
    val on: Boolean
)
