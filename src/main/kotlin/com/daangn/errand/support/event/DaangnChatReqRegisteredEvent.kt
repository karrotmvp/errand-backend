package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.*

interface DaangnChatReqRegisteredEvent {
    val targetUserIds: List<String>
    val title: String
    val text: String
    val linkUrl: String
    val buttonText: String
    val actionType: ActionType
    fun buildBizChat() =
        targetUserIds.asSequence().map { userId ->
            val action = actionType.name
            PostBizChatReq(
                Input(userId, title, text, listOf(Action(action, Payload(linkUrl, buttonText))))
            )
        }.toList()
}