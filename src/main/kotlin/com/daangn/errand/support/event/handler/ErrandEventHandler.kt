package com.daangn.errand.support.event.handler

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.service.MixpanelService
import com.daangn.errand.service.MixpanelTrackEvent
import com.daangn.errand.service.daangn.DaangnOpenApiService
import com.daangn.errand.service.daangn.dto.ActionType
import com.daangn.errand.service.daangn.dto.DaangnChatRequest
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.ErrandCreatedEvent
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.RedisUtil
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.stream.Collectors

private const val ERRAND_CREATED_CHAT_IMAGE_URL = "https://errandbucket.s3.ap-northeast-2.amazonaws.com/errand/static/%E1%84%89%E1%85%A2%E1%84%85%E1%85%A9%E1%84%8B%E1%85%AE%E1%86%AB%E1%84%89%E1%85%B5%E1%86%B7%E1%84%87%E1%85%AE%E1%84%85%E1%85%B3%E1%86%B7_%E1%84%8B%E1%85%A1%E1%86%AF%E1%84%85%E1%85%B5%E1%86%B7%E1%84%90%E1%85%A9%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png"

@Component
class ErrandEventHandler(
        @Value("\${host.url}")
        private val baseUrl: String,
        private val redisUtil: RedisUtil,
        private val errandRepository: ErrandRepository,
        private val userRepository: UserRepository,
        private val daangnOpenAPIService: DaangnOpenApiService,
        private val mixpanelService: MixpanelService,
) {

    private val logger = KotlinLogging.logger { }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun requestToSendNewErrandNotification(event: ErrandCreatedEvent) {
        val errand = errandRepository.findById(event.errandId)
                .orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val apiRequest =
                DaangnChatRequest(
                        targetUserIds = getTargetUserDaangnIds(errand),
                        title = "${getRegionName(errand) ?: "우리동네"}에 새로운 심부름이 올라왔어요.",
                        text = "심부름의 자세한 내용을 확인해보세요!",
                        linkUrl = "$baseUrl/errands/${errand.id}",
                        buttonText = "보러 갈래요",
                        actionType = ActionType.NORMAL_BUTTON,
                        imageUrl = ERRAND_CREATED_CHAT_IMAGE_URL,
                ).toApiRequest()
        daangnOpenAPIService.sendBizChatting(apiRequest)
    }

    private fun getRegionName(errand: Errand): String? =
            try {
                daangnOpenAPIService.getRegionInfoByRegionId(errand.regionId).region.name
            } catch (e: Exception) {
                null
            }


    fun getTargetUserDaangnIds(errand: Errand): List<String> {
        val neighborRegionResponse = daangnOpenAPIService.getNeighborRegionByRegionId(errand.regionId)
        val userIdsInRegion = neighborRegionResponse.getRegionIds().stream()
                .flatMap { regionId -> redisUtil.getDaangnUserIdsBy(regionId).stream() }
                .collect(Collectors.toSet())

        val users = userRepository.findUsersInDaangnIdsAndHavingCategory(
                userIdsInRegion,
                errand.category.id!!,
        )
        return users.stream()
                .filter { user -> user.id != errand.customer.id }
                .map { user -> user.daangnId }
                .collect(Collectors.toList())
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendMixpanelEvent(event: ErrandCreatedEvent) {
        val errand = errandRepository.findById(event.errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }

        val userInfo = daangnOpenAPIService.getUserProfile(errand.customer.daangnId).data.user
        val entities = mapOf(
                Pair("심부름 id", errand.id.toString()),
                Pair("심부름 카테고리", errand.category.name),
                Pair("유저 ID", userInfo.id),
                Pair("유저 닉네임", userInfo.nickname ?: "닉네임 미등록"),
        )

        mixpanelService.trackEvent(MixpanelTrackEvent.ERRAND_REGISTERED, entities)
        logger.info("send to mixpanel event: ${MixpanelTrackEvent.ERRAND_REGISTERED.korName}")
    }
}
