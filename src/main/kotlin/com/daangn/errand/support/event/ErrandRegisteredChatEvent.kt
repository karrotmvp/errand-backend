package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType


class ErrandRegisteredChatEvent(
    override var targetUserIds: List<String>,
    override val linkUrl: String,
    val regionName: String?,
) : DaangnChatReqRegisteredEvent {
    override val title = if (regionName != null) "${regionName}에 새로운 심부름이 올라왔어요." else "우리 동네에 새로운 심부름이 올라왔어요."
    override val text = "심부름의 자세한 내용을 확인해보세요!"
    override val buttonText = "보러 갈래요"
    override val actionType = ActionType.NORMAL_BUTTON
}