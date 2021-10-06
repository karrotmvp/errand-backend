package com.daangn.errand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ErrandApplication

fun main(args: Array<String>) {
    runApplication<ErrandApplication>(*args)
}
