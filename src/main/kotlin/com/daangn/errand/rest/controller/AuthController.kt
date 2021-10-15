package com.daangn.errand.rest.controller

import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.service.AuthService
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.JwtUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/auth")
@Api(tags = ["인증 관련 API"])
class AuthController(
    val authService: AuthService,
    val userService: UserService,
    val jwtUtil: JwtUtil
) {
    @GetMapping("")
    @ApiOperation(value = "로그인 api")
    fun login(
        @RequestParam(value = "authCode") authCode: String,
        res: HttpServletResponse
    ): ErrandResponse<Any> {
        val accessToken = authService.getAccessToken(authCode)
        val userProfile = authService.getUserProfile(accessToken)

        val user: UserVo = userService.loginOrSignup(userProfile)

        val cookie = Cookie("token", jwtUtil.generateToken(JwtPayload(user.id!!, accessToken)))
        cookie.maxAge = 60 * 60 * 24
        cookie.isHttpOnly = true

        res.addCookie(cookie)
        return ErrandResponse(HttpStatus.OK, "로그인 성공")
    }
}