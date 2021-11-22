package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType

class MatchingRegisteredChatEvent(
    override val targetUserIds: List<String>,
    override val linkUrl: String
): DaangnChatReqRegisteredEvent {
    override val title = "지원한 심부름에 매칭되었어요!"
    override val text = "심부름 장소와 전화번호를 확인하고 심부름을 진행하세요."
    override val actionType = ActionType.NORMAL_BUTTON
    override val buttonText = "상세정보 확인하기"
    override val imageUrl = "https://errandbucket.s3.ap-northeast-2.amazonaws.com/errand/static/%E1%84%86%E1%85%A2%E1%84%8E%E1%85%B5%E1%86%BC%E1%84%8B%E1%85%AA%E1%86%AB%E1%84%85%E1%85%AD_%E1%84%8B%E1%85%A1%E1%86%AF%E1%84%85%E1%85%B5%E1%86%B7%E1%84%90%E1%85%A9%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png"
}