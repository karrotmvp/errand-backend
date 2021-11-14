package com.daangn.errand.support.event.publisher

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.ErrandRegisteredChatEvent
import com.daangn.errand.support.event.HelpRegisteredChatEvent
import com.daangn.errand.support.event.MatchingAfterChatEvent
import com.daangn.errand.support.event.MatchingRegisteredChatEvent
import com.daangn.errand.support.event.scheduler.EventScheduler
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.RedisUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
data class DaangnChatEventPublisher(
    @Value("\${host.url}")
    private val baseUrl: String,
    private val eventPublisher: ApplicationEventPublisher,
    private val daangnUtil: DaangnUtil,
    private val redisUtil: RedisUtil,
    private val userRepository: UserRepository,
    private val errandRepository: ErrandRepository,
    private val eventScheduler: EventScheduler,
) {
    @Async
    @Transactional
    fun publishErrandRegisteredEvent(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val targetUserList = getUserDaangnIdListInCategory(errand)
        val buttonLinkedUrl = "$baseUrl/errands/$errandId"
        val regionName = daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
        eventPublisher.publishEvent(ErrandRegisteredChatEvent(targetUserList, buttonLinkedUrl, regionName))
    }

    fun getUserDaangnIdListInCategory(errand: Errand): List<String> {
        val category = errand.category
        val neighborUsers: MutableSet<String> = HashSet()
        val neighborRegionIdList =
            daangnUtil.getNeighborRegionByRegionId(errand.regionId).data.region.neighborRegions.map { region ->
                region.id
            }
        val iterator = neighborRegionIdList.iterator()
        while (iterator.hasNext()) {
            neighborUsers.addAll(redisUtil.getDaangnIdListByRegionId(iterator.next()))
        }
        val users = userRepository.findByDaangnIdListAndHasCategory(errand.customer, neighborUsers, category)

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
    }

    @Async
    @Transactional
    fun publishMatchingAfterChatEvent(helperDaangnId: String, errandId: Long) {
        eventScheduler.addElement(
            MatchingAfterChatEvent(listOf(helperDaangnId), "$baseUrl/errands/${errandId}"),
            LocalDateTime.now().plusHours(24)
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
    }
}
