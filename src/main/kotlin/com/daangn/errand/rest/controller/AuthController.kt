package com.daangn.errand.rest.controller

import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.rest.dto.auth.LoginResDto
import com.daangn.errand.service.AuthService
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.JwtUtil
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/auth")
@Api(tags = ["인증 관련 API"])
class AuthController(
    private val authService: AuthService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {
    @PostMapping("")
    @ApiOperation(value = "로그인 api")
    fun login(
        @ApiParam(value = "당근 API access code") @RequestParam(value = "authCode") authCode: String,
        @ApiParam(value = "지역 ID") @RequestParam(value = "regionId") regionId: String,
        res: HttpServletResponse
    ): ErrandResponse<LoginResDto> {
        val accessToken = authService.getAccessToken(authCode)
        val userProfile = authService.getUserProfile(accessToken)

        val user: UserVo = userService.loginOrSignup(userProfile, accessToken)
        val token = jwtUtil.generateToken(JwtPayload(user.id!!, accessToken))
        userService.saveLastRegionId(user.daangnId, regionId) // save last region id
        return ErrandResponse(LoginResDto(token, user.id))
    }
}