package com.daangn.errand.support.exception

import com.daangn.errand.support.error.ErrandError

class ErrandException: RuntimeException {
    var error: ErrandError = ErrandError.CUSTOM_ERROR

    constructor(error: ErrandError): super(error.description) { this.error = error }
    constructor(error: ErrandError, message: String): super(error.description + "($message)") { this.error = error }
}