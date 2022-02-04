package com.daangn.errand.support.event.handler

import com.daangn.errand.service.daangn.DaangnOpenApiService
import com.daangn.errand.support.event.DaangnChatReqRegisteredEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DaangnChatReqEventHandler(
    private val daangnOpenAPIService: DaangnOpenApiService
) {
    private val logger = KotlinLogging.logger { }

    @Async
    @EventListener
    fun sendBizChat(event: DaangnChatReqRegisteredEvent) {
        event.buildBizChat().forEach { reqDto ->
            daangnOpenAPIService.sendBizChatting(reqDto)
        }
        logger.info(
            "succeed to send biz chat: ${event.title} to ${event.targetUserIds.size} users(${
                event.targetUserIds.joinToString(
                    ", "
                )
            })."
        )
    }
}
