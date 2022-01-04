package com.daangn.errand.util

import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

@Component
class AsyncS3UploaderImpl(
    private val s3AsyncClient: S3AsyncClient,
) : AsyncS3Uploader {
    private val BUCKET_NAME: String = "BUCKET_NAME"
    override val logger: KLogger = KotlinLogging.logger { }

    override fun putObject(key: String, file: File): CompletableFuture<PutObjectResponse> {
        val objectRequest = PutObjectRequest
            .builder().bucket(BUCKET_NAME).key(key).build()
        return s3AsyncClient.putObject(objectRequest, AsyncRequestBody.fromFile(Path.of(file.path)))
            .whenComplete { _, _ -> removeNewFile(file) }
    }
}