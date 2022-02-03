package com.daangn.errand.util.daangn

import com.daangn.errand.util.DaangnUtil
import org.springframework.stereotype.Component

@Component
class ChatSender(
        private val daangnUtil: DaangnUtil
) {
    fun sendBizChat(request: DaangnChatRequest) {
        request.toApiRequest().parallelStream()
                .map(daangnUtil::sendBizChatting)
    }
}
