package com.daangn.errand.service.daangn.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetAccessTokenRes(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    val scope: String
)

