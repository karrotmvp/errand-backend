package com.daangn.errand.support.event.publisher

import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
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
    private val errandRepository: ErrandRepository,
    private val userRepository: UserRepository
) {
    @Async
    @Transactional(readOnly = true)
    fun publishErrandRegisteredEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val entities: HashMap<String, Any> = HashMap()
        entities["심부름 id"] = errand.id.toString()
        entities["심부름 카테고리"] = errand.category.name

        val userInfo = daangnUtil.getUserInfo(errand.customer.daangnId).data.user
        entities["유저 ID"] = userInfo.id
        entities["유저 닉네임"] = userInfo.nickname ?: "닉네임 미등록"

//        entities["customer_errandReqList_size"] = errand.customer.errandReqList.size.toString() // 믹스패널에서 조회 가능
        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.ERRAND_REGISTERED, entities))
    }

    @Async
    @Transactional(readOnly = true)
    fun publishHelpRegisteredEvent(helpId: Long) {
        val help = helpRepository.findById(helpId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val entities: HashMap<String, Any> = HashMap()

        entities["심부름 id"] = help.errand.id.toString()
        entities["심부름 카테고리"] = help.errand.category.name
        entities["지원 순서"] = help.errand.helps.size.toString()

        val userInfo = daangnUtil.getUserInfo(help.helper.daangnId).data.user
        entities["헬퍼 ID"] = userInfo.id
        entities["헬퍼 닉네임"] = userInfo.nickname ?: "닉네임 미등록"

        val helpCnt = helpRepository.countByHelper(help.helper)
//        entities["helper_help_size"] = helpCnt.toString()
//        entities["helper_errandList_size"] = help.helper.errandList.size.toString()

        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.HELP_REGISTERED, entities))
    }

    @Async
    @Transactional(readOnly = true)
    fun publishErrandCompletedEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val entities = HashMap<String, Any>()

        entities["심부름 ID"] = errand.id.toString()
        entities["심부름 카테고리"] = errand.category.name
        entities["고객 ID"] = errand.customer.id.toString()

        val customerInfo = daangnUtil.getUserInfo(errand.customer.daangnId).data.user
        val helperInfo = daangnUtil.getUserInfo(
            errand.chosenHelper?.daangnId ?: throw ErrandException(
                ErrandError.ENTITY_NOT_FOUND,
                "chosen helper 없음"
            )
        ).data.user
        entities["고객 닉네임"] = customerInfo.nickname ?: "닉네임 미등록"
        entities["헬퍼 닉네임"] = helperInfo.nickname ?: "닉네임 미등록"
        entities["고객 가입일자"] = errand.customer.createdAt.toString()
        entities["헬퍼 가입일자"] = errand.chosenHelper?.createdAt.toString()

        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.ERRAND_COMPLETED, entities))
    }

    @Async
    @Transactional(readOnly = true)
    fun publishErrandSignInEvent(userId: Long, isSignUp: Boolean) {
        // 최초 회원가입시 유저가 데이터베이스에 저장되지 않은 상태이므로,
        // 서비스 완료 후 컨트롤러에서 이벤트를 발생시키자.
        val user = userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val userInfo = daangnUtil.getUserInfo(user.daangnId).data.user
        val entities = HashMap<String, Any>()
        entities["최초 로그인?"] = isSignUp
        entities["유저 ID"] = userId
        entities["유저 닉네임"] = userInfo.nickname ?: "미등록 닉네임"
        entities["유저 가입일"] = user.createdAt

        eventPublisher.publishEvent(MixpanelEvent(MixpanelTrackEvent.USER_SIGN_IN, entities))
    }
}