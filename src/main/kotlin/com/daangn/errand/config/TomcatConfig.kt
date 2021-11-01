package com.daangn.errand.config

import org.apache.catalina.Context
import org.apache.tomcat.util.http.LegacyCookieProcessor
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

@Component
class CustomContainer(
    val contextCustomizer: TomcatContextCustomizer
) : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory?) {
        factory?.addContextCustomizers(contextCustomizer)
    }
}

@Component
class ErrandContextCustomizer : TomcatContextCustomizer {
    override fun customize(context: Context?) {
        context?.cookieProcessor = LegacyCookieProcessor()
    }
}