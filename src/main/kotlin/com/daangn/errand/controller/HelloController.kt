package com.daangn.errand.controller

import com.daangn.errand.support.dto.UploadImageDto
import com.daangn.errand.support.dto.UploadImagesDto
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.S3Uploader
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.view.RedirectView
import springfox.documentation.annotations.ApiIgnore
import java.time.LocalDateTime
import javax.servlet.http.HttpServletResponse

@RestController
@Api(tags = ["Health check"])
@RequestMapping("")
class HelloController(
    val s3Uploader: S3Uploader
) {
    @GetMapping("")
    @ApiOperation(value = "health check uri")
    fun healthCheck() = ResponseEntity<String>("Healthy.", null, HttpStatus.OK)

    @GetMapping("/api-docs")
    @ApiIgnore
    fun apiDocs() = RedirectView("/swagger-ui/")

    @PostMapping("/test/file")
    @ApiOperation(value = "파일 업로드 테스트 API")
    fun uploadFile(
       @ModelAttribute uploadImageDto: UploadImageDto
    ) = ResponseEntity<String>(s3Uploader.upload(
        uploadImageDto.img, uploadImageDto.fileName, "errand/test"
    ), null, HttpStatus.OK)

    @PostMapping("/test/files")
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
}