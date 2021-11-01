package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType

class MatchingAfterChatEvent(
    override val targetUserIds: List<String>,
    override val linkUrl: String
): DaangnChatReqRegisteredEvent {
    override val title: String = "심부름을 완수하셨나요?"
    override val text = "그렇다면 완료 버튼을 눌러주세요. 도움을 주신 내역이 기록될거예요."
    override val actionType = ActionType.NORMAL_BUTTON
    override val buttonText: String = "완료 누르러가기"
}