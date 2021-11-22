package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType

class MatchingAfterChatEvent(
    override val targetUserIds: List<String>,
    override val linkUrl: String
): DaangnChatReqRegisteredEvent {
    override val title: String = "심부름을 완료하셨나요?"
    override val text = "심부름을 완료했다면 완료버튼을 눌러주세요."
    override val actionType = ActionType.NORMAL_BUTTON
    override val buttonText: String = "완료 누르러가기"
    override val imageUrl = "https://errandbucket.s3.ap-northeast-2.amazonaws.com/errand/static/%E1%84%89%E1%85%B5%E1%86%B7%E1%84%87%E1%85%AE%E1%84%85%E1%85%B3%E1%86%B7%E1%84%8B%E1%85%AA%E1%86%AB%E1%84%85%E1%85%AD_%E1%84%8B%E1%85%A1%E1%86%AF%E1%84%85%E1%85%B5%E1%86%B7%E1%84%90%E1%85%A9%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png"
}