package com.daangn.errand.support.exception

import io.sentry.Sentry
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class SentrySender {
    @Async
    fun sendToSentry(e: Exception) {
        Sentry.captureException(e)
    }
}