package com.daangn.errand.rest.controller

import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.rest.dto.category.PatchUserCategoryReqDto
import com.daangn.errand.rest.dto.PatchUserAlarmReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import javax.websocket.server.PathParam

@RestController
@RequestMapping("/user")
@Api(tags = ["유저 관련 API"])
class UserController(
    val userService: UserService,
) {
    @GetMapping("/{id}")
    @ApiOperation(value = "사용자의 당근 프로필 가져오기")
    fun getUserProfile(
        @ApiParam(value = "사용자의 id") @PathVariable(value = "id") id: Long
    ): ErrandResponse<UserProfileVo> {
        return ErrandResponse(userService.getUserProfileWithDaangnInfo(id))
    }

    @GetMapping("/my")
    @ApiOperation(value = "나의 당근 프로필 가져오기")
    fun getMyProfile(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "지역 ID") @PathParam(value = "regionId") regionId: String
    ): ErrandResponse<UserProfileVo> {
        return ErrandResponse(userService.getMyProfileWithDaangnInfo(payload.userId, regionId))
    }

    @PatchMapping("/category")
    fun patchUserCategory(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @RequestBody patchUserCategoryReqDto: PatchUserCategoryReqDto
    ): ErrandResponse<Any> {
        if (patchUserCategoryReqDto.on)
            userService.setCategory(payload.userId, patchUserCategoryReqDto.categoryId)
        else userService.deactivateCategory(payload.userId, patchUserCategoryReqDto.categoryId)
        return ErrandResponse(HttpStatus.OK)
    }

    @PatchMapping("/alarm")
    @ApiOperation(value = "헬퍼 지원시 알림톡 전송 설정 API")
    fun setAlarm(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @RequestBody patchUserAlarmReqDto: PatchUserAlarmReqDto
    ): ErrandResponse<Any> {
        val result = userService.updateUserAlarm(payload.userId, patchUserAlarmReqDto.on)
        return ErrandResponse(HttpStatus.OK, result)
    }

    @GetMapping("/alarm")
    @ApiOperation("알림 설정 여부를 조회하는 API")
    fun getAlarm(
        @ApiIgnore @TokenPayload payload: JwtPayload
    ) = ErrandResponse(userService.readUserAlarm(payload.userId))
}