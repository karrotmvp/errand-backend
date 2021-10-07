package com.daangn.errand.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("")
    fun helloWorld() = ResponseEntity<String>("Hello daangn!", null, HttpStatus.OK)
}