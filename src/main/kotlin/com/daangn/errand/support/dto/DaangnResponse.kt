package com.daangn.errand.support.dto

import java.time.LocalDateTime

interface DaangnResponse {
    val status: Int
    val message: String
    val timestamp: LocalDateTime
}