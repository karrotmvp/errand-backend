package com.daangn.errand.support.exception

import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.response.ErrandResponse
import io.sentry.Sentry
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException

@RestControllerAdvice
class GlobalExceptionHandler(
    val sentrySender: SentrySender
) {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(ErrandException::class)
    fun errandException(e: ErrandException): ResponseEntity<ErrandResponse<Unit>> {
        sentrySender.sendToSentry(e)
        val error: ErrandError = e.error
        logger.error { "ErrandException : ${e.message}" }
        return ResponseEntity
            .status(error.status)
            .body(ErrandResponse(error.status, e.message ?: "알 수 없는 에러"))
    }

    @ExceptionHandler(MultipartException::class)
    fun multipartException(e: MultipartException): ResponseEntity<ErrandResponse<Unit>> {
        sentrySender.sendToSentry(e)
        logger.error { "MultipartException : ${e.stackTrace}" }
        val msg: String = if (e is MaxUploadSizeExceededException) "이미지 사이즈가 너무 커요." else "이미지가 입력되지 않았습니다."
        return ResponseEntity
            .status(400)
            .body(ErrandResponse(HttpStatus.BAD_REQUEST, msg))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrandResponse<Unit>> {
        sentrySender.sendToSentry(e)
        logger.error { "MethodArgumentNotValidException : " + e.message }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrandResponse(HttpStatus.BAD_REQUEST, e.message))
    }

    @ExceptionHandler(RuntimeException::class)
    fun unknownException(e: Exception): ResponseEntity<ErrandResponse<Unit>> {
        logger.error { "UnknownException : $e" }
        sentrySender.sendToSentry(e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrandResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러"))
    }
}