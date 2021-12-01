package com.daangn.errand.support.event.handler

import com.daangn.errand.service.MixpanelService
import com.daangn.errand.support.event.MixpanelEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component // 이벤트 리스너를 빈으로 올려준다
class MixpanelEventHandler(
    private val mixpanelService: MixpanelService
) {
    private val logger = KotlinLogging.logger {  }
    @Async // 다른 스레드에서 동작
    @EventListener
    fun sendEventToMixpanel(event: MixpanelEvent) {
        logger.info("send to mixpanel event: ${event.type.korName}")
        mixpanelService.trackEvent(event.type, event.entities)
    }
}