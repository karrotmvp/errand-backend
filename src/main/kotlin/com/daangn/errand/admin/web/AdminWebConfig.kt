package com.daangn.errand.admin.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AdminWebConfig(
    val loginInterceptor: LoginInterceptor
): WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns("/admin/**")
            .excludePathPatterns("/admin")
            .excludePathPatterns("/admin/login")
    }
}