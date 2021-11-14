package com.daangn.errand.support.event.handler

import com.daangn.errand.support.event.DaangnChatReqRegisteredEvent
import com.daangn.errand.util.DaangnUtil
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DaangnChatReqEventHandler(
    val daangnUtil: DaangnUtil
) {
    @Async
    @EventListener
    fun sendBizChat(event: DaangnChatReqRegisteredEvent) {
        event.buildBizChat().forEach { reqDto ->
            daangnUtil.sendBizChatting(reqDto)
        }
    }
}