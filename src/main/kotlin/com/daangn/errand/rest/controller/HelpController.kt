package com.daangn.errand.rest.controller

import com.daangn.errand.domain.help.HelpVo
import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.service.HelpService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/help")
@Api(tags = ["지원하기(Help) 관련 API"])
class HelpController(
    private val helpService: HelpService,
    private val errandService: ErrandService,
) {
    @GetMapping("/{helpId}")
    @ApiOperation(value = "지원 ID로 지원 내역 보기 API")
    fun getHelpDetail(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "Help ID") @PathVariable(value = "helpId") id: Long
    ) = ErrandResponse(errandService.readHelpDetail(payload, id))


    @PostMapping
    fun postHelp(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @RequestBody postHelpReqDto: PostHelpReqDto
    ): ErrandResponse<HelpVo> {
        val helpVo = helpService.createHelp(payload.userId, postHelpReqDto)
        return ErrandResponse(helpVo)
    }

    @DeleteMapping("/{id}")
    fun deleteHelp(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "Help ID") @PathVariable(value = "id") helpId: Long
    ): ErrandResponse<Any> {
        helpService.destroyHelp(payload.userId, helpId)
        return ErrandResponse(HttpStatus.OK, "지원 취소 성공")
    }
}