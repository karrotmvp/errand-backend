package com.daangn.errand.support.error

import org.springframework.http.HttpStatus

enum class ErrandError(var description: String, val status: HttpStatus) {
    FAIL_TO_CREATE("생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_LOGIN("비 로그인 사용자", HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("사용자의 입력이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    NOT_PERMITTED("권한이 없는 유저 입니다.", HttpStatus.FORBIDDEN),
    DUPLICATE("중복 입력입니다.", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND("엔터티를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPECTED_VALUE("예상하지 못한 값입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETD_USER("삭제된 유저입니다.", HttpStatus.FORBIDDEN),
    AWS_ERROR("AWS 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    CUSTOM_ERROR("", HttpStatus.INTERNAL_SERVER_ERROR);

    fun setDescExceptionMsg(e: Exception): ErrandError {
        description = e.toString()
        return this
    }

    fun setCustomDesc(msg: String): ErrandError {
        description = msg
        return this
    }
}