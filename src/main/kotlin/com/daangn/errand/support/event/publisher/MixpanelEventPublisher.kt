package com.daangn.errand.support.event.publisher

import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.service.MixpanelTrackEvent
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.MixpanelEvent
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MixpanelEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
    private val daangnUtil: DaangnUtil,
    private val helpRepository: HelpRepository,
    private val errandRepository: ErrandRepository
) {
    @Async
    @Transactional(readOnly = true)
    fun publishErrandRegisteredEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val entities: HashMap<String, String> = HashMap()
        entities["errand_id"] = errand.id.toString()
        entities["errand_category"] = errand.category.name

        val userInfo = daangnUtil.getUserInfo(errand.customer.daangnId).data.user
        entities["customer_daangn_id"] = userInfo.id
        entities["customer_daangn_nickname"] = userInfo.nickname ?: "닉네임 미등록"

        entities["customer_errandReqList_size"] = errand.customer.errandReqList.size.toString()
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

    @Async
    @Transactional(readOnly = true)
    fun publishErrandCompletedEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val entities = HashMap<String, String>()

        entities["errand_id"] = errand.id.toString()
        entities["errand_category"] = errand.category.name
        entities["customer_id"] = errand.customer.id.toString()

        val customerInfo = daangnUtil.getUserInfo(errand.customer.daangnId).data.user
        val helperInfo = daangnUtil.getUserInfo(
            errand.chosenHelper?.daangnId ?: throw ErrandException(
                ErrandError.ENTITY_NOT_FOUND,
                "chosen helper 없음"
            )
        ).data.user
        entities["customer_nickname"] = customerInfo.nickname ?: "닉네임 미등록"
        entities["helper_nickname"] = helperInfo.nickname ?: "닉네임 미등록"
        entities["customer_created_at"] = errand.customer.createdAt.toString()
        entities["helper_created_at"] = errand.chosenHelper?.createdAt.toString()

        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.ERRAND_COMPLETED, entities))
    }
}