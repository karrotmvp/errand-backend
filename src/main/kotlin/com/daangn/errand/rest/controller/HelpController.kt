package com.daangn.errand.rest.controller

import com.daangn.errand.domain.help.HelpVo
import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.HelpService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/help")
class HelpController(
    val helpService: HelpService
) {
    @PostMapping
    fun postHelp(
        @TokenPayload payload: JwtPayload,
        @RequestBody postHelpReqDto: PostHelpReqDto
    ): ErrandResponse<HelpVo> {
        val helpVo = helpService.createHelp(payload.userId, postHelpReqDto)
        return ErrandResponse(helpVo)
    }

    @DeleteMapping("/{id}")
    fun deleteHelp(
        @TokenPayload payload: JwtPayload,
        @PathVariable(value = "id") helpId: Long
    ): ErrandResponse<Any> {
        helpService.destroyHelp(payload.userId, helpId)
        return ErrandResponse(HttpStatus.OK, "지원 취소 성공")
    }
}