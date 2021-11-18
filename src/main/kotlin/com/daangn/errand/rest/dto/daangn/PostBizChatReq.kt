package com.daangn.errand.rest.dto.daangn

import com.fasterxml.jackson.annotation.JsonInclude

data class PostBizChatReq(
    val input: Input
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Input(
    val userId: String,
    val title: String,
    val text: String,
    val actions: List<Action>,
    val imageUrl: String? = null
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