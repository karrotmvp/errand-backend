package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType

class MatchingRegisteredEvent(
    override val targetUserIds: List<String>,
    override val linkUrl: String
): DaangnChatReqRegisteredEvent {
    override val title = "지원한 심부름에 매칭되었어요!"
    override val text = "심부름의 상세주소와 전화번호를 확인한 후 심부름을 수행해주세요."
    override val actionType = ActionType.NORMAL_BUTTON
    override val buttonText = "상세정보 확인하기"
}