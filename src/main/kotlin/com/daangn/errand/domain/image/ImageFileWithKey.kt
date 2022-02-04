package com.daangn.errand.domain.image

import org.springframework.web.multipart.MultipartFile

data class ImageFileWithKey(
    val image: MultipartFile,
    val key: String,
)
