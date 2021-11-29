package com.daangn.errand.support.event.publisher

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandDto
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.*
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.RedisUtil
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
data class DaangnChatEventPublisher(
    @Value("\${host.url}")
    private val baseUrl: String,
    private val eventPublisher: ApplicationEventPublisher,
    private val daangnUtil: DaangnUtil,
    private val redisUtil: RedisUtil,
    private val userRepository: UserRepository,
    private val errandRepository: ErrandRepository,
) {
    private val logger = KotlinLogging.logger { }

    @Async
//    @Transactional
    fun publishErrandRegisteredEvent(errandDto: ErrandDto) {
        var errand: Errand? = null
        for (i in 1..3) {
            errand =
                errandRepository.findById(errandDto.id!!).orElse(null)
            if (errand != null) break
            Thread.sleep(300)
        }
        if (errand == null) throw ErrandException(ErrandError.ENTITY_NOT_FOUND, "알림챗을 보내기 위한 심부름 엔티티 조회 실패")
        val targetUserList = getUserDaangnIdListInCategory(errandDto, errand.regionId)
        val buttonLinkedUrl = "$baseUrl/errands/${errandDto.id}"
        val regionName = try {
            daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
        } catch (e: Exception) {
            null
        }
        eventPublisher.publishEvent(ErrandRegisteredChatEvent(targetUserList, buttonLinkedUrl, regionName))
        logger.info("publish errand registered event published now.")
    }

    fun getUserDaangnIdListInCategory(errandDto: ErrandDto, regionId: String): List<String> {
        val neighborUsers: MutableSet<String> = HashSet()
        val neighborRegionIdList =
            daangnUtil.getNeighborRegionByRegionId(regionId).data.region.neighborRegions.map { region ->
                region.id
            }
        val iterator = neighborRegionIdList.iterator()
        while (iterator.hasNext()) {
            neighborUsers.addAll(redisUtil.getDaangnIdListByRegionId(iterator.next()))
        }
        val users = userRepository.findByDaangnIdListAndHasCategory(
            errandDto.customer.id!!,
            neighborUsers,
            errandDto.category.id!!
        )

        return users.asSequence().map { user ->
            user.daangnId
        }.toList()
    }

    @Async
    @Transactional
    fun publishMatchingRegisteredEvent(helperDaangnId: String, errandId: Long) {
        eventPublisher.publishEvent(
            MatchingRegisteredChatEvent(
                listOf(helperDaangnId),
                "$baseUrl/errands/$errandId"
            )
        )
        logger.info("matching registered event published")
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
    @Transactional
    fun publishHelpRegisteredEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        eventPublisher.publishEvent(
            HelpRegisteredChatEvent(
                listOf(errand.customer.daangnId),
                "$baseUrl/errands/${errandId}"
            )
        )
        logger.info("help registered event published")
    }
}
