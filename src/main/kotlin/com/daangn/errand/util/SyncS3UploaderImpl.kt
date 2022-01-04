package com.daangn.errand.util

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.PutObjectRequest
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import kotlin.jvm.Throws

@Component
class SyncS3UploaderImpl(
    private val s3Client: AmazonS3Client
): SyncS3Uploader {
    private val BUCKET_NAME: String = "BUCKET_NAME"
    override val logger = KotlinLogging.logger {  }

    override fun uploadFileAndGetFileUrl(multipartFile: MultipartFile, fileName: String, dirName: String?): String {
        val file = convertMultipartToFile(multipartFile)
        val key: String = generateKey(dirName, fileName)
        val fileUrl = putObject(key, file)
        removeNewFile(file)
        return fileUrl
    }

    @Throws(AmazonS3Exception::class)
    override fun putObject(key: String, file: File): String {
        val uploadParams = PutObjectRequest(BUCKET_NAME, key, file)
        s3Client.putObject(uploadParams)
        return s3Client.getUrl(BUCKET_NAME, key).toString()
    }
}