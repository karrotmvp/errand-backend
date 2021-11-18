package com.daangn.errand.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MixpanelConfig(
    @Value("\${application.mixpanel.token}")
    val token: String
)
