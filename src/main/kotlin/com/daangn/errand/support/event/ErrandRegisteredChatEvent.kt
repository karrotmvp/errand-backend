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
    override val imageUrl: String = "https://errandbucket.s3.ap-northeast-2.amazonaws.com/errand/static/%E1%84%89%E1%85%A2%E1%84%85%E1%85%A9%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%89%E1%85%B5%E1%86%B7%E1%84%87%E1%85%AE%E1%84%85%E1%85%B3%E1%86%B7_%E1%84%8B%E1%85%A1%E1%86%AF%E1%84%85%E1%85%B5%E1%86%B7%E1%84%90%E1%85%A9%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png"
}