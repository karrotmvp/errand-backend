package com.daangn.errand.support.event

import com.daangn.errand.service.MixpanelTrackEvent

// 믹스패널에 보내야하는 데이터를 가지고 있는 이벤트 객체
data class MixpanelEvent(val type: MixpanelTrackEvent, val entities: HashMap<String, Any>)