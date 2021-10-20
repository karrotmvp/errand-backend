package com.daangn.errand.support.event.handler

import com.daangn.errand.support.event.DaangnChatReqRegisteredEvent
import com.daangn.errand.util.DaangnUtil
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DaangnChatReqEventHandler(
    val daangnUtil: DaangnUtil
) {
    @EventListener
    fun sendBizChat(event: DaangnChatReqRegisteredEvent) {
        println("알림톡 전송한다링~")
         event.buildBizChat().forEach { reqDto ->
             daangnUtil.sendBizChatting(reqDto)
         }
    }
}