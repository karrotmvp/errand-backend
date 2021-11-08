package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.errand.PatchHelperOfErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.rest.dto.help.HelpCountResDto
import com.daangn.errand.rest.dto.help.HelperWithHelpId
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.service.HelpService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@RestController
@Api(tags = ["심부름 관련 API"])
@RequestMapping("/errand")
class ErrandController(
    val errandService: ErrandService,
    val helpService: HelpService
) {
    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ApiOperation(value = "심부름을 등록하는 API")
    fun postErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ModelAttribute postErrandReqDto: PostErrandReqDto
    ): ErrandResponse<PostErrandResDto> {
        return ErrandResponse(errandService.createErrand(payload.userId, postErrandReqDto))
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "심부름 상세 조회 API")
    fun getErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "심부름 ID") @PathVariable(value = "id") errandId: Long
    ) = ErrandResponse(errandService.readErrand(payload, errandId))

    @GetMapping("/{id}/helpers")
    @ApiOperation(value = "심부름 지원 유저 목록 조회 API")
    fun getErrandAppliedUserList(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "심부름 ID") @PathVariable(value = "id") errandId: Long
    ): ErrandResponse<List<HelperWithHelpId>> = ErrandResponse(errandService.readAppliedHelpers(payload, errandId))

    @PatchMapping("/{id}/helper")
    @ApiOperation(value = "심부름 헬퍼 지정하기 API")
    fun patchHelperOfErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "심부름 ID") @PathVariable(value = "id") id: Long,
        @RequestBody patchHelperOfErrandReqDto: PatchHelperOfErrandReqDto
    ): ErrandResponse<Any> {
        errandService.chooseHelper(payload.userId, patchHelperOfErrandReqDto.helperId, id)
        return ErrandResponse(HttpStatus.OK, "심부름에 헬퍼 지정 성공")
    }

    @PatchMapping("/{id}/complete")
    @ApiOperation(value = "심부름 완료 확정하기 API")
    fun patchProgressOfErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "심부름 ID") @PathVariable(value = "id") errandId: Long
    ): ErrandResponse<Any> {
        errandService.confirmErrand(payload.userId, errandId)
        return ErrandResponse(HttpStatus.OK, "심부름 완료 확정 성공")
    }

    @GetMapping("/{id}/helper-count")
    @ApiOperation(value = "심부름 지원자 수 조회하기 API")
    fun getHelperCount(
        @ApiParam(value = "심부름 ID") @PathVariable(value = "id") errandId: Long
    ) = ErrandResponse(HelpCountResDto(helpService.countHelp(errandId)))

    @DeleteMapping("/{id}")
    @ApiOperation(value = "심부름 삭제하기 API")
    fun deleteErrand(
        @TokenPayload payload: JwtPayload,
        @ApiParam(value = "심부름 ID") @PathVariable(value = "id") errandId: Long
    ): ErrandResponse<Any> {
        errandService.destroyErrand(payload.userId, errandId)
        return ErrandResponse(HttpStatus.OK, "심부름 삭제 성공")
    }
}