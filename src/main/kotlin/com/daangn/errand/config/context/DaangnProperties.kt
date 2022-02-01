package com.daangn.errand.config.context

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "daangn")
data class DaangnProperties(
    val oapi: OAPI,
    val openApi: OpenAPI,
    val appAuth: String,
    val appKey: String,
) {
    data class OAPI(val neighborRange: String, val url: String)
    data class OpenAPI(val url: String)
}
