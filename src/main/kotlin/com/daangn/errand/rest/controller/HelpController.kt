package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.HelpService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/help")
class HelpController(
    val helpService: HelpService
) {
    @PostMapping
    fun postHelp(
        @TokenPayload payload: JwtPayload,
        @RequestBody postHelpReqDto: PostHelpReqDto
    ) = ErrandResponse(helpService.createHelp(payload.userId, postHelpReqDto))
}