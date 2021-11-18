package com.daangn.errand.rest.dto.auth

data class LoginResDto(
    val token: String,
    val userId: Long,
)
