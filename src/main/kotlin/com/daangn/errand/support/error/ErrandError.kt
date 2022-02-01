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
    FAIL_TO_LOGIN("로그인 실패", HttpStatus.FORBIDDEN),
    AWS_ERROR("AWS 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPECTED_ERROR("서버 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPOSED_ERRAND("미노출된 게시물입니다.", HttpStatus.BAD_REQUEST),
    DAANGN_ERROR("당근 API 에러", HttpStatus.INTERNAL_SERVER_ERROR);
}