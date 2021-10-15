package com.daangn.errand.rest.dto

import io.swagger.annotations.ApiModelProperty

class User {

}

data class LogInResponseDto(
    @ApiModelProperty(value = "token") // TODO
    val token: String,
) {
}