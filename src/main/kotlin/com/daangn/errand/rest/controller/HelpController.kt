package com.daangn.errand.rest.controller

import com.daangn.errand.domain.help.HelpVo
import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.HelpService
import com.daangn.errand.service.MixpanelService
import com.daangn.errand.service.MixpanelTrackEvent
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/help")
class HelpController(
    val helpService: HelpService,
    val mixpanelService: MixpanelService
) {
    @PostMapping
    fun postHelp(
        @TokenPayload payload: JwtPayload,
        @RequestBody postHelpReqDto: PostHelpReqDto
    ): ErrandResponse<HelpVo> {
        // TODO: 이벤트마다 track event의 entity를 만드는 책임 분리
        val entities = HashMap<String, String>()
        entities.put("userId", payload.userId.toString())
        mixpanelService.trackEvent(MixpanelTrackEvent.HELP_REGISTERED, entities) // TODO: 실제 이벤트로 리팩토링
        return ErrandResponse(helpService.createHelp(payload.userId, postHelpReqDto))
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