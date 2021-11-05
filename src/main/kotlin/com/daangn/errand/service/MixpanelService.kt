package com.daangn.errand.service

import com.daangn.errand.config.MixpanelConfig
import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class MixpanelService(
    val mixpanelConfig: MixpanelConfig
) {
    @Throws(IOException::class)
    fun trackEvent(event: MixpanelTrackEvent, entities: HashMap<String, Any>) {
        val messageBuilder = MessageBuilder(mixpanelConfig.token)

        val props = JSONObject()
        entities.forEach { entity ->
            props.put(entity.key, entity.value)
        }

        val jsonObject: JSONObject = messageBuilder.event(null, event.korName, props)

        val delivery = ClientDelivery()
        delivery.addMessage(jsonObject)

        val mixpanel = MixpanelAPI()
        mixpanel.deliver(delivery)
    }
}

enum class MixpanelTrackEvent(val korName: String) {
    ERRAND_REGISTERED("새로운 심부름 등록"),
    HELP_REGISTERED("새로운 지원"),
    ERRAND_COMPLETED("심부름 완료"),
    USER_SIGN_IN("로그인"),
}