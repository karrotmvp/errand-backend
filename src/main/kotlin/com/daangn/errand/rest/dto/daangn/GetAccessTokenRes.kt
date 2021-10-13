package com.daangn.errand.rest.dto.daangn

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GetAccessTokenRes(
    override val status: Int,
    override val message: String,
    override val timestamp: LocalDateTime,
    val data: Data
) : DaangnResponse {
    data class Data(
        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("token_type")
        val tokenType: String,

        @JsonProperty("expires_in")
        val expiresIn: Long,

        val scope: String
    )
}

