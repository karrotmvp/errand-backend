package com.daangn.errand.util

import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.File
import java.util.concurrent.CompletableFuture

interface AsyncS3Uploader: S3Uploader {
    fun putObject(key: String, file: File): CompletableFuture<PutObjectResponse>
}