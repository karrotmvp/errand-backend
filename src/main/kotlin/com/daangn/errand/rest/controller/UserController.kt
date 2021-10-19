package com.daangn.errand.rest.controller

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.websocket.server.PathParam

@RestController
@RequestMapping("/user")
class UserController(
    val userService: UserService
) {
    @GetMapping("/my")
    fun getMyProfile(
        @TokenPayload payload: JwtPayload,
        @PathParam(value = "regionId") regionId: String
    ): ErrandResponse<UserProfileVo> {
        return ErrandResponse(userService.getUserWithDaangnProfile(payload.userId, payload.accessToken, regionId))
    }
}