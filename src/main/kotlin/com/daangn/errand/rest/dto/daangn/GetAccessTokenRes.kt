package com.daangn.errand.rest.dto.daangn

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GetAccessTokenRes(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    val scope: String
)

