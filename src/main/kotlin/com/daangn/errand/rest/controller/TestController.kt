package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.UploadImageDto
import com.daangn.errand.rest.dto.UploadImagesDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.S3Uploader
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import java.time.LocalDateTime

@RestController
@ApiIgnore
@RequestMapping("/test")
class TestController(
    val s3Uploader: S3Uploader
) {

    @PostMapping("/file")
    @ApiOperation(value = "파일 업로드 테스트 API")
    fun uploadFile(
       @ModelAttribute uploadImageDto: UploadImageDto
    ) = ResponseEntity<String>(s3Uploader.upload(
        uploadImageDto.img, uploadImageDto.fileName, "errand/test"
    ), null, HttpStatus.OK)

    @PostMapping("/files")
    @ApiOperation(value = "파일 여러개 업로드 테스트 API")
    fun uploadFiles(
        @ModelAttribute uploadImagesDto: UploadImagesDto
    ): ErrandResponse<List<String>> {
        val multipartFiles = uploadImagesDto.img
        val urls: List<String> = multipartFiles.asSequence().map { multipartFile ->
            val fileName = LocalDateTime.now().toString()
            s3Uploader.upload(multipartFile, fileName, "errand/test")
        }.toList()
        return ErrandResponse(urls)
    }

    @PostMapping("/token")
    @ApiIgnore
    fun cookieArgument(@TokenPayload payload: JwtPayload) = ErrandResponse(payload)
}