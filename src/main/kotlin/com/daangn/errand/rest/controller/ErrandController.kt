package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.errand.PatchHelperOfErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.help.HelpCountResDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.service.HelpService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import javax.websocket.server.PathParam

@RestController
@Api(tags = ["심부름 관련 API"])
@RequestMapping("/errand")
class ErrandController(
    val errandService: ErrandService,
    val helpService: HelpService
) {
    @PostMapping("")
    @ApiOperation(value = "심부름을 등록하는 API")
    fun postErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @RequestBody postErrandReqDto: PostErrandReqDto
    ) = ErrandResponse(errandService.createErrand(payload.userId, postErrandReqDto))

    @GetMapping("/{id}")
    @ApiOperation(value = "심부름 상세 조회 API")
    fun getErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @PathVariable(value = "id") id: Long
    ) = ErrandResponse(errandService.readErrand(payload, id))

    @GetMapping("/{id}/helpers")
    @ApiOperation(value = "심부름 지원 유저 목록 조회 API")
    fun getErrandAppliedUserList(
        @TokenPayload payload: JwtPayload,
        @PathVariable(value = "id") id: Long
    ) = ErrandResponse(errandService.readAppliedHelpers(payload, id))

    @PatchMapping("/{id}/helper")
    fun patchHelperOfErrand(
        @TokenPayload payload: JwtPayload,
        @PathVariable(value = "id") id: Long,
        @RequestBody patchHelperOfErrandReqDto: PatchHelperOfErrandReqDto
    ): ErrandResponse<Any> {
        errandService.chooseHelper(payload.userId, patchHelperOfErrandReqDto.helperId, id)
        return ErrandResponse(HttpStatus.OK, "심부름에 헬퍼 지정 성공")
    }

    @PatchMapping("/{id}/complete")
    fun patchProgressOfErrand(
        @TokenPayload payload: JwtPayload,
        @PathVariable(value = "id") id: Long
    ): ErrandResponse<Any> {
        errandService.confirmErrand(payload.userId, id)
        return ErrandResponse(HttpStatus.OK, "심부름 완료 확정 성공")
    }

    @GetMapping("/{id}/helper-count")
    fun getHelperCount(
        @PathVariable(value = "id") id: Long
    ) = ErrandResponse(HelpCountResDto(helpService.countHelp(id)))

    @GetMapping("/{id}/helpers/{helpId}")
    fun getHelperDetail(
        @TokenPayload payload: JwtPayload,
        @PathVariable(value = "helpId") id: Long
    ) = ErrandResponse(errandService.readHelperDetail(payload, id))
}