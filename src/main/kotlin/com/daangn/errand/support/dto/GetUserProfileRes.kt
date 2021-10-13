package com.daangn.errand.support.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GetUserProfileRes(
    override val status: Int,
    override val message: String,
    override val timestamp: LocalDateTime,
    val data: Data
) : DaangnResponse {
    data class Data(
        val nickname: String,

        @JsonProperty("profile_image_url")
        val profileImageUrl: String?,

        @JsonProperty("user_id")
        val userId: String
    )
}

