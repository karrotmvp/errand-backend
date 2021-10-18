package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.service.HelpService
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/help")
class HelpController(
    val userService: UserService,
    val helpService: HelpService
) {
    @PostMapping
    fun postHelp(
        @RequestBody postHelpReqDto: PostHelpReqDto
    ) {
        ErrandResponse(helpService.createHelp(postHelpReqDto))
    }
}