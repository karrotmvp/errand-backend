package com.daangn.errand.rest.dto.errand

import org.springframework.web.multipart.MultipartFile

data class PostErrandReqDto(
    val categoryId: Long,
    val detail: String,
    val reward: String,
    val detailAddress: String,
    val phoneNumber: String,
    val regionId: String,
    val images: List<MultipartFile>?
)