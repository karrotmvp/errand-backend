package com.daangn.errand.support.event.publisher

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.service.MixpanelTrackEvent
import com.daangn.errand.support.event.MixpanelEvent
import com.daangn.errand.util.DaangnUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class MixpanelEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
    private val daangnUtil: DaangnUtil
){
    @Async
    fun publishErrandRegisteredEvent(errand: Errand) {
        val userInfo = daangnUtil.getUserInfo(errand.customer.daangnId).data.user
        val entities: HashMap<String, String> = HashMap()
        entities["심부름 ID (errand id)"] = errand.id!!.toString()
        entities["심부름 요청 유저의 당근 ID"] = userInfo.id
        entities["심부름 요청 유저의 당근 닉네임"] = userInfo.nickname ?: "유저"
        entities["심부름 카테고리 ID"] = errand.category.id.toString()
        entities["심부름 카테고리"]  = errand.category.name
        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.ERRAND_REGISTERED, entities))
    }
}