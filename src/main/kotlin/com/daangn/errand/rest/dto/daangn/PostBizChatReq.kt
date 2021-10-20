package com.daangn.errand.rest.dto.daangn

data class PostBizChatReq(
    val input: Input
)

data class Input(
    val userId: String,
    val title: String,
    val text: String,
    val actions: List<Action>,
)

data class Action(
    val type: String,
    val payload: Payload
)

data class Payload(
    val linkUrl: String,
    val text: String
)

enum class ActionType {
    PRIMARY_BUTTON,
    NORMAL_BUTTON
}