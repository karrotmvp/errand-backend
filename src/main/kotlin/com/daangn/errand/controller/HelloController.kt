package com.daangn.errand.controller

import com.daangn.errand.support.dto.UploadImageDto
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

    @PostMapping("/test/img")
    @ApiOperation(value = "이미지 업로드 테스트 API")
    fun uploadImage(
       @ModelAttribute uploadImageDto: UploadImageDto
    ) = ResponseEntity<String>(s3Uploader.upload(
        uploadImageDto.img, uploadImageDto.fileName, "errand/img"
    ), null, HttpStatus.OK)
}