package com.daangn.errand.rest.dto.errand

data class PostErrandReqDto (
    val categoryId: Long,
    val title: String,
    val detail: String,
    val reward: String,
    val detailAddress: String,
    val phoneNumber: String,
    val regionId: String
        )