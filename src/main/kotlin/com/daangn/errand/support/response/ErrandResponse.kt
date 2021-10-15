package com.daangn.errand.support.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrandResponse<T>(var data: T?, var status: HttpStatus?, var statusCode: Int?, var message: String?) {
    constructor(data: T) : this(data, HttpStatus.OK, 200, null)
    constructor(data: T, status: HttpStatus) : this(data, status, status.value(), null)
    constructor(status: HttpStatus, message: String) : this(null, status, status.value(), message)
}