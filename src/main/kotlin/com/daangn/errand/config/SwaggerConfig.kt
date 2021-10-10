package com.daangn.errand.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
class SwaggerConfig {
    @Bean
    fun api(): Docket =
        Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.daangn.errand.controller"))
            .paths(PathSelectors.any())
            .build()

    private fun apiInfo(): ApiInfo = ApiInfoBuilder()
        .title("당근 심부름 API 문서")
        .build()
}