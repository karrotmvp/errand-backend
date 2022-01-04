package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.UploadImageDto
import com.daangn.errand.rest.dto.UploadImagesDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.SyncS3Uploader
import io.swagger.annotations.ApiOperation
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import java.time.LocalDateTime
import javax.websocket.server.PathParam

@RestController
@ApiIgnore
@RequestMapping("/test")
class TestController(
    val s3Uploader: SyncS3Uploader,
    val redisTemplate: RedisTemplate<String, String>
) {

    @PostMapping("/file")
    @ApiOperation(value = "파일 업로드 테스트 API")
    fun uploadFile(
        @ModelAttribute uploadImageDto: UploadImageDto
    ) = ResponseEntity<String>(
        s3Uploader.uploadFileAndGetFileUrl(
            uploadImageDto.img, uploadImageDto.fileName, "errand/test"
        ), null, HttpStatus.OK
    )

    @PostMapping("/files")
    @ApiOperation(value = "파일 여러개 업로드 테스트 API")
    fun uploadFiles(
        @ModelAttribute uploadImagesDto: UploadImagesDto
    ): ErrandResponse<List<String>> {
        val multipartFiles = uploadImagesDto.img
        val urls: List<String> = multipartFiles.asSequence().map { multipartFile ->
            val fileName = LocalDateTime.now().toString()
            s3Uploader.uploadFileAndGetFileUrl(multipartFile, fileName, "errand/test")
        }.toList()
        return ErrandResponse(urls)
    }

    @PostMapping("/token")
    @ApiIgnore
    fun cookieArgument(@TokenPayload payload: JwtPayload) = ErrandResponse(payload)

    @PostMapping("/redis")
    @ApiIgnore
    fun setKeyValueInElasticache(
        @PathParam(value = "key") key: String,
        @PathParam(value = "value ") value: String
    ): ErrandResponse<String> {
        redisTemplate.opsForValue().set(key, value)
        return ErrandResponse("[Key: $key, value: $value] redis-cli로 확인해보세요")
    }
}