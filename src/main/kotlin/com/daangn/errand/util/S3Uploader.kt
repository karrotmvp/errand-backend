package com.daangn.errand.util

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.PutObjectRequest
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import kotlin.jvm.Throws

@Component
class S3Uploader(
    val s3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}")
    val bucketName: String
) {
    private val logger = KotlinLogging.logger {  }

    fun upload(multipartFile: MultipartFile, fileName: String, dirName: String? = null): String {
        val file = convertMultipartToFile(multipartFile)
        val dirPrefix: String = if (dirName != null) "$dirName/" else ""
        val key: String = dirPrefix + fileName
        val fileUrl = putObject(key, file)
        removeNewFile(file)
        return fileUrl
    }

    @Throws(AmazonS3Exception::class)
    fun putObject(key: String, file: File): String {
        val uploadParams = PutObjectRequest(bucketName, key, file)
        s3Client.putObject(uploadParams)
        return s3Client.getUrl(bucketName, key).toString()
    }

    fun convertMultipartToFile(multipartfile: MultipartFile): File {
        val convertedFile = File(System.getProperty("user.dir") + "/" + multipartfile.originalFilename)
        if (convertedFile.createNewFile()) {
            val fos = FileOutputStream(convertedFile)
            fos.write(multipartfile.bytes)
            return convertedFile
        }
        throw IllegalStateException("파일 변환 실패")
    }

    fun removeNewFile(targetFile: File) {
        if(targetFile.delete()) {
            logger.info("File delete success")
            return
        }
        logger.info("File $targetFile delete Fail")
    }
}