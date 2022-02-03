package com.daangn.errand.util.daangn

import com.daangn.errand.rest.dto.daangn.*

data class DaangnChatRequest(
    val targetUserIds: List<String>,
    val title: String,
    val text: String,
    val linkUrl: String,
    val buttonText: String,
    val actionType: ActionType,
    val imageUrl: String,
) {

    fun toApiRequest(): List<PostBizChatReq> =
        targetUserIds.asSequence()
            .map { userId ->
                val action = actionType.name
                PostBizChatReq(
                        Input(userId, title, text, listOf(Action(action, Payload(linkUrl, buttonText))), imageUrl)
                )
            }
            .toList()
}
