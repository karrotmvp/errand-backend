package com.daangn.errand.util

import mu.KLogger
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

interface S3Uploader {
    val logger: KLogger

    // default method
    fun generateKey(dirName: String?, fileName: String): String {
        val dirPrefix: String = if (dirName != null) "$dirName/" else ""
        val key: String = dirPrefix + fileName
        return key
    }

    // default method
    fun generateObjectUrl(bucketName: String, key: String): String {
        return "https://${bucketName}.s3.ap-northeast-2.amazonaws.com/$key"
    }

    // default method
    fun convertMultipartToFile(multipartfile: MultipartFile): File {
        val convertedFile = File(System.getProperty("user.dir") + "/" + multipartfile.originalFilename)
        if (convertedFile.createNewFile()) {
            val fos = FileOutputStream(convertedFile)
            fos.write(multipartfile.bytes)
            return convertedFile
        }
        throw IllegalStateException("파일 변환 실패")
    }

    // default method
    fun removeNewFile(targetFile: File) {
        if(targetFile.delete()) {
            logger.info("File delete success")
            return
        }
        logger.info("File $targetFile delete Fail")
    }
}