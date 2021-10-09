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
    fun helloWorld() = ResponseEntity<String>("Hello daangn!", null, HttpStatus.OK)

    @GetMapping("/api")
    fun testAPI() = ResponseEntity<String>("OK", null, HttpStatus.OK)
}