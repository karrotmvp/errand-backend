package com.daangn.errand.rest.dto.daangn

import java.time.LocalDateTime

interface DaangnResponse {
    val status: Int
    val message: String?
    val timestamp: LocalDateTime
}