package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.*

interface DaangnChatReqRegisteredEvent {
    val targetUserIds: List<String>
    val title: String
    val text: String
    val linkUrl: String // TODO: 리팩토링 - 링크 이벤트 객체가 만들도록 하기
    val buttonText: String
    val actionType: ActionType
    fun buildBizChat() =
        targetUserIds.asSequence().map { userId ->
            val action = actionType.name
            PostBizChatReq(
                Input(userId, title, text, listOf(Action(action, Payload(linkUrl, buttonText))), "https://errandbucket.s3.ap-northeast-2.amazonaws.com/errand/static/%E1%84%83%E1%85%A1%E1%86%BC%E1%84%80%E1%85%B3%E1%86%AB%E1%84%89%E1%85%B5%E1%86%B7%E1%84%87%E1%85%AE%E1%84%85%E1%85%B3%E1%86%B7+%E1%84%8B%E1%85%A1%E1%86%AF%E1%84%85%E1%85%B5%E1%86%B7%E1%84%90%E1%85%A9%E1%86%A8.png")
            )
        }.toList()
}