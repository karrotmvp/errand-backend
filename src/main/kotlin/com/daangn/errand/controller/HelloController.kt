package com.daangn.errand.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["Test API"])
class HelloController {
    @GetMapping("")
    fun healthCheck() = ResponseEntity<String>("Healthy.", null, HttpStatus.OK)

    @GetMapping("/api")
    @ApiOperation(value = "test API")
    fun testAPI() = ResponseEntity<String>("OK", null, HttpStatus.OK)
}