package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.*

interface DaangnChatReqRegisteredEvent {
    val targetUserIds: List<String>
    val title: String
    val text: String
    val linkUrl: String // TODO: 리팩토링 - 링크 이벤트 객체가 만들도록 하기
    val buttonText: String
    val actionType: ActionType
    val imageUrl: String
    fun buildBizChat() =
        targetUserIds.asSequence().map { userId ->
            val action = actionType.name
            PostBizChatReq(
                Input(userId, title, text, listOf(Action(action, Payload(linkUrl, buttonText))), imageUrl)
            )
        }.toList()
}