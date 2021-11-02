package com.daangn.errand.rest.controller

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.category.PatchUserCategoryReqDto
import com.daangn.errand.rest.dto.patchUserAlarmReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
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

    @PatchMapping("/category")
    fun patchUserCategory(
        @TokenPayload payload: JwtPayload,
        @RequestBody patchUserCategoryReqDto: PatchUserCategoryReqDto
    ): ErrandResponse<Any> {
        if (patchUserCategoryReqDto.on)
            userService.setCategory(payload.userId, patchUserCategoryReqDto.categoryId)
        else userService.deactivateCategory(payload.userId, patchUserCategoryReqDto.categoryId)
        return ErrandResponse(HttpStatus.OK)
    }

    @PatchMapping("/alarm")
    fun setAlarm(
        @TokenPayload payload: JwtPayload,
        @RequestBody patchUserAlarmReqDto: patchUserAlarmReqDto
    ): ErrandResponse<Any> {
        val result = userService.updateUserAlarm(payload.userId, patchUserAlarmReqDto.on)
        return ErrandResponse(HttpStatus.OK, result)
    }

    @GetMapping("/alarm")
    fun getAlarm(
        @TokenPayload payload: JwtPayload
    ) = ErrandResponse(userService.readUserAlarm(payload.userId))
}