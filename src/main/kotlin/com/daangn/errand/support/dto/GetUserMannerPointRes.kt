package com.daangn.errand.support.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GetUserMannerPointRes(
    override val status: Int,
    override val message: String,
    override val timestamp: LocalDateTime,
    val data: Data?
) : DaangnResponse {
    data class Data(
        @JsonProperty("manner_point")
        val mannerPoint: Float
    )
}
