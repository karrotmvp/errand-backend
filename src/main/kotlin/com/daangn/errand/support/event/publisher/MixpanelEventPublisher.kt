package com.daangn.errand.support.event.publisher

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.help.Help
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.service.MixpanelTrackEvent
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.MixpanelEvent
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class MixpanelEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
    private val daangnUtil: DaangnUtil,
    private val helpRepository: HelpRepository,
) {
    @Async
    fun publishErrandRegisteredEvent(errand: Errand) {
        val userInfo = daangnUtil.getUserInfo(errand.customer.daangnId).data.user
        val customer = userRepository.findById(errand.customer.id!!)
            .orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val entities: HashMap<String, String> = HashMap()
        entities["심부름 ID (errand id)"] = errand.id.toString()
        entities["심부름 카테고리"] = errand.category.name

        entities["심부름 요청 유저의 당근 ID"] = userInfo.id
        entities["심부름 요청 유저의 당근 닉네임"] = userInfo.nickname ?: "닉네임 미등록"

        entities["이 유저의 심부름 등록 횟수"] = errand.customer.errandReqList.size.toString()
        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.ERRAND_REGISTERED, entities))
    }

    @Async
    @Transactional(readOnly = true)
    fun publishHelpRegisteredEvent(helpId: Long) {
        val help = helpRepository.findById(helpId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val entities: HashMap<String, String> = HashMap()

        entities["errand_id"] = help.errand.id.toString()
        entities["errand_category"] = help.errand.category.name
        entities["errand_help_count"] = help.errand.helps.size.toString()

        val userInfo = daangnUtil.getUserInfo(help.helper.daangnId).data.user
        entities["user_daangn_id"] = userInfo.id
        entities["helper_nickname"] = userInfo.nickname ?: "닉네임 미등록"

        val helpCnt = helpRepository.countByHelper(help.helper)
        entities["helper_help_size"] = helpCnt.toString()
        entities["helper_errandList_size"] = help.helper.errandList.size.toString()

        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.HELP_REGISTERED, entities))
    }
}