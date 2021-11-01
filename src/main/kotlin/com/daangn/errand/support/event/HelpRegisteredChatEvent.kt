package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType

class HelpRegisteredChatEvent(
    override val targetUserIds: List<String>,
    override val linkUrl: String,
): DaangnChatReqRegisteredEvent {
    override val title = "새로운 이웃이 심부름에 지원했어요!"
    override val text = "내용을 확인해보세요."
    override val buttonText = "지원자 전체 보기"
    override val actionType = ActionType.NORMAL_BUTTON
}