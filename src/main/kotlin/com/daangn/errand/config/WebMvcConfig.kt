package com.daangn.errand.config

import com.daangn.errand.rest.resolver.TokenPayloadArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Profile(value = ["dev", "local"])
class DevWebMvcConfig(
    val tokenPayloadArgumentResolver: TokenPayloadArgumentResolver
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(tokenPayloadArgumentResolver)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .maxAge(3000)
            .allowedHeaders("x-requested-with, origin, content-type, accept")
    }
}

@Configuration
@Profile("prod")
class WebMvcConfig(
    val tokenPayloadArgumentResolver: TokenPayloadArgumentResolver
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(tokenPayloadArgumentResolver)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("https://daangn-errand.com", "http://daangn-errand.com")
            .allowCredentials(true)
            .allowedMethods("HEAD", "GET", "POST", "PATCH", "OPTIONS")
            .maxAge(3000)
            .allowedHeaders("x-requested-with, origin, content-type, accept")
    }
}