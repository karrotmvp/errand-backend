package com.daangn.errand.support.response

import org.springframework.http.HttpStatus

class ErrandResponse<T>(var data: T?, var status: HttpStatus?, var statusCode: Int?, var message: String?){
    constructor(data: T) : this(data, HttpStatus.OK, 200, null)
    constructor(data: T, status: HttpStatus) : this(data, status, status.value(), null)
    constructor(status: HttpStatus, message: String) : this(null, status, status.value(), message)
}