package com.daangn.errand.support.event.publisher

import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.service.daangn.DaangnOpenApiService
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.*
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.RedisUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
data class DaangnChatEventPublisher(
        @Value("\${host.url}")
    private val baseUrl: String,
        private val eventPublisher: ApplicationEventPublisher,
        private val daangnOpenAPIService: DaangnOpenApiService,
        private val redisUtil: RedisUtil,
        private val userRepository: UserRepository,
        private val errandRepository: ErrandRepository,
) {

    @Async
    fun publishMatchingRegisteredEvent(helperDaangnId: String, errandId: Long) {
        eventPublisher.publishEvent(
            MatchingRegisteredChatEvent(
                listOf(helperDaangnId),
                "$baseUrl/errands/$errandId"
            )
        )
    }

    @Async
    fun publishMatchingAfterChatEvent(helperDaangnId: String, errandId: Long) { // 완료 알림톡 이벤트를 발행
        eventPublisher.publishEvent(
            MatchingAfterChatEvent(
                listOf(helperDaangnId),
                "${baseUrl}/errands/${errandId}"
            )
        )
    }

    @Async
    fun publishMakeCompleteNotiEntityEvent(errandId: Long) { // CompleteNotiEvent 엔티티 insert 이벤트를 발행
        eventPublisher.publishEvent(
            MakeCompleteNotiEntityEvent(
                errandId
            )
        )
    }

    @Async
    fun publishHelpRegisteredEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        eventPublisher.publishEvent(
            HelpRegisteredChatEvent(
                listOf(errand.customer.daangnId),
                "$baseUrl/errands/${errandId}"
            )
        )
    }
}
