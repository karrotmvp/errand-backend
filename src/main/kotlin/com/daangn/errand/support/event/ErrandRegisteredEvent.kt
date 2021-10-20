package com.daangn.errand.support.event

import com.daangn.errand.rest.dto.daangn.ActionType
import org.springframework.beans.factory.annotation.Value


class ErrandRegisteredEvent(
    override var targetUserIds: List<String>,
    val errandId: Long,
    override val linkUrl: String
): DaangnChatReqRegisteredEvent {
    override val title = "우리 동네에 심부름이 올라왔어요"
    override val text = "심부름의 자세한 내용을 확인해보세요!"
    override val buttonText = "보러 가요"
    override val actionType = ActionType.NORMAL_BUTTON
}