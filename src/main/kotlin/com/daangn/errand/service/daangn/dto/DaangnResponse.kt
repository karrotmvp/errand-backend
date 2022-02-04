package com.daangn.errand.service.daangn.dto

import java.time.LocalDateTime

interface DaangnResponse {
    val status: Int
    val message: String?
    val timestamp: LocalDateTime
}
