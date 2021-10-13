package com.daangn.errand.rest.controller

import com.daangn.errand.service.UserService
import com.daangn.errand.util.DaangnUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController(value = "/auth")
@Api(tags = ["인증 관련 API"])
class AuthController(
    val daangnUtil: DaangnUtil,
    val userService: UserService
) {
    @GetMapping("/")
    @ApiOperation(value = "로그인 api")
    fun login(@RequestHeader(value = "Authorization-code") accessCode: String) {
        val getAccessTokenRes = daangnUtil.getAccessTokenByOpenApi(accessCode)
        val userInfo = daangnUtil.getUserInfo(getAccessTokenRes.accessToken)
        val user = userService.loginOrSignup(userInfo)
    }
}