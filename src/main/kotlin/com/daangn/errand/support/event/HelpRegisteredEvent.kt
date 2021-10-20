package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType

class HelpRegisteredEvent(
    override val targetUserIds: List<String>,
    override val linkUrl: String,
): DaangnChatReqRegisteredEvent {
    override val title = "새로운 이웃이 심부름에 지원했어요!"
    override val text = "지원 내용을 확인해보세요."
    override val buttonText = "확인하러 갈래요"
    override val actionType = ActionType.NORMAL_BUTTON
}