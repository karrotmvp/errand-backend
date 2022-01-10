package com.daangn.errand.util

import org.springframework.web.multipart.MultipartFile
import java.io.File

interface SyncS3Uploader: S3Uploader {
    fun uploadFileAndGetFileUrl(multipartFile: MultipartFile, fileName: String, dirName: String? = null): String
    fun putObject(key: String, file: File): String
}