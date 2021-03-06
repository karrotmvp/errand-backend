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
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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
    @Async
    @Transactional
    fun publishErrandRegisteredEvent(errandDto: ErrandDto) {
        val errand =
            errandRepository.findById(errandDto.id!!).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val targetUserList = getUserDaangnIdListInCategory(errand, errand.regionId)
        val buttonLinkedUrl = "$baseUrl/errands/${errandDto.id}"
        val regionName = try {
            daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
        } catch (e: Exception) {
            null
        }
        eventPublisher.publishEvent(ErrandRegisteredChatEvent(targetUserList, buttonLinkedUrl, regionName))
    }

    fun getUserDaangnIdListInCategory(errand: Errand, regionId: String): List<String> {
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
            errand.customer.id!!,
            neighborUsers,
            errand.category.id!!
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
    }

    @Async
    fun publishMatchingAfterChatEvent(helperDaangnId: String, errandId: Long) { // ?????? ????????? ???????????? ??????
        eventPublisher.publishEvent(
            MatchingAfterChatEvent(
                listOf(helperDaangnId),
                "${baseUrl}/errands/${errandId}"
            )
        )
    }

    @Async
    fun publishMakeCompleteNotiEntityEvent(errandId: Long) { // CompleteNotiEvent ????????? insert ???????????? ??????
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
    }
}
