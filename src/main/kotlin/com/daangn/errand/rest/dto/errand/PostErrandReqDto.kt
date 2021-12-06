package com.daangn.errand.rest.dto.errand

import io.swagger.annotations.ApiModelProperty
import org.springframework.web.multipart.MultipartFile

data class PostErrandReqDto(
    @ApiModelProperty(value = "카테고리 ID")
    val categoryId: Long,
    @ApiModelProperty(value = "심부르 상세 정보")
    val detail: String,
    @ApiModelProperty(value = "사례")
    val reward: String,
    @ApiModelProperty(value = "상세주소", required = false)
    val detailAddress: String? = null,
    @ApiModelProperty(value = "요청 유저의 전화번호")
    val phoneNumber: String,
    @ApiModelProperty(value = "지역 ID")
    val regionId: String,
    @ApiModelProperty(value = "이미지", required = false)
    val images: List<String>?
)