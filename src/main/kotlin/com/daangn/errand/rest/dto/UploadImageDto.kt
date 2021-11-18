package com.daangn.errand.rest.dto

import org.springframework.web.multipart.MultipartFile

data class UploadImageDto(
    val img: MultipartFile,
    val fileName: String
)

data class UploadImagesDto(
    val img: List<MultipartFile>
)
