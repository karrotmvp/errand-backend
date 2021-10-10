package com.daangn.errand.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import springfox.documentation.annotations.ApiIgnore
import javax.servlet.http.HttpServletResponse

@RestController
@Api(tags = ["Health check"])
@RequestMapping("")
class HelloController {
    @GetMapping("")
    @ApiOperation(value = "health check uri")
    fun healthCheck() = ResponseEntity<String>("Healthy.", null, HttpStatus.OK)

    @GetMapping("/api-docs")
    @ApiIgnore
    fun apiDocs() = RedirectView("/swagger-ui/")
}