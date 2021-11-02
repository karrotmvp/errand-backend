package com.daangn.errand.service

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.errand.ErrandPreview
import com.daangn.errand.domain.errand.Status
import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.repository.*
import com.daangn.errand.rest.dto.daangn.Region
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.rest.dto.help.GetHelpDetailResDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.ErrandRegisteredChatEvent
import com.daangn.errand.support.event.MatchingAfterChatEvent
import com.daangn.errand.support.event.MatchingRegisteredChatEvent
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.event.scheduler.EventScheduler
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.RedisUtil
import com.daangn.errand.util.S3Uploader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ErrandService(
    @Value("\${host.url}")
    private val baseUrl: String,

    private val userRepository: UserRepository,
    private val errandRepository: ErrandRepository,
    private val categoryRepository: CategoryRepository,
    private val helpRepository: HelpRepository,
    private val imageRepository: ImageRepository,

    private val errandConverter: ErrandConverter,
    private val regionConverter: RegionConverter,
    private val userConverter: UserConverter,
    private val s3Uploader: S3Uploader,

    private val eventPublisher: ApplicationEventPublisher,
    private val eventScheduler: EventScheduler,
    private val mixpanelEventPublisher: MixpanelEventPublisher,

    private val daangnUtil: DaangnUtil,
    private val redisUtil: RedisUtil,

    ) {
    fun createErrand(userId: Long, postErrandReqDto: PostErrandReqDto): PostErrandResDto {
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category = categoryRepository.findById(postErrandReqDto.categoryId).orElseThrow {
            throw ErrandException(ErrandError.BAD_REQUEST)
        }
        if (postErrandReqDto.images.size > 5) throw ErrandException(ErrandError.BAD_REQUEST, "사진은 최대 5장 첨부 가능합니다.")
        val errand = errandRepository.save(
            Errand(
                category = category,
                detail = postErrandReqDto.detail,
                reward = postErrandReqDto.reward,
                detailAddress = postErrandReqDto.detailAddress,
                customerPhoneNumber = postErrandReqDto.phoneNumber,
                customer = user,
                regionId = postErrandReqDto.regionId
            )
        )
        postErrandReqDto.images.forEach { image ->
            val fileName = "${errand.id}-${user.id}-${LocalDateTime.now()}"
            val imageUrl = s3Uploader.upload(image, fileName, "errand/img")
            imageRepository.save(Image(imageUrl, errand))
        }
        val errandId = errand.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE)
        val res = PostErrandResDto(errandId)

        val list = getUserDaangnIdListInCategory(errand)
        val linkUrl = "$baseUrl/errands/$errandId"
        eventPublisher.publishEvent(ErrandRegisteredChatEvent(list, linkUrl)) // TODO: 비동기로 바꾸기
        mixpanelEventPublisher.publishErrandRegisteredEvent(errand.id!!)
        return res
    }

    fun getUserDaangnIdListInCategory(errand: Errand): List<String> {
        val category = errand.category
        val neighborUsers: MutableSet<String> = HashSet()
        val neighborIdList =
            daangnUtil.getNeighborRegionByRegionId(errand.regionId).data.region.neighborRegions.map { region ->
                region.id
            }
        val iterator = neighborIdList.iterator()
        while (iterator.hasNext()) {
            neighborUsers.addAll(redisUtil.getDaangnIdListByRegionId(iterator.next()))
        }
        val users = userRepository.findByDaangnIdListAndHasCategory(neighborUsers, category)

        return users.asSequence().map { user ->
            user.daangnId
        }.toList()
    }

    fun readErrand(payload: JwtPayload, errandId: Long): GetErrandResDto { // TODO: 리팩토링
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val user =
            userRepository.findById(payload.userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val isMine = errand.customer == user
        val didIApply: Boolean = !isMine && helpRepository.findByErrandAndHelper(errand, user) != null
        val wasIChosen = didIApply && errand.chosenHelper == user
        val errandDto = errandConverter.toErrandDto(errand)
        errandDto.region = regionConverter.toRegionVo(daangnUtil.getRegionInfoByRegionId(errand.regionId).region)
        if (!isMine && !wasIChosen) {
            errandDto.customerPhoneNumber = null
            errandDto.detailAddress = null
        }
        return GetErrandResDto(
            errand = errandDto,
            isMine = isMine,
            didIApply = didIApply,
            wasIChosen = wasIChosen
        )
    }

    fun readAppliedHelpers(payload: JwtPayload, errandId: Long): List<UserProfileVo> {
        val (userId, accessToken) = payload
        val errand = errandRepository.findById(errandId)
            .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 아이디의 심부름이 존재하지 않습니다.") }
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        return helpRepository.findByErrandOrderByCreatedAt(errand).asSequence().map { help ->
            val userProfileVo =
                daangnUtil.setUserDetailProfile(
                    userConverter.toUserProfileVo(help.helper),
                    accessToken,
                    help.regionId
                ) // TODO 다시하기
            userProfileVo.regionName = daangnUtil.getRegionInfoByRegionId(help.regionId).region.name
            userProfileVo
        }.toList()
    }

    fun chooseHelper(userId: Long, helperId: Long, errandId: Long) {
        val errand = errandRepository.findById(errandId)
            .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 id의 심부름을 찾을 수 없습니다.") }
        if (errand.chosenHelper != null) throw ErrandException(ErrandError.BAD_REQUEST, "이미 지정된 헬퍼가 있습니다.")
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)

        val helper = userRepository.findById(helperId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.chosenHelper = helper

        eventPublisher.publishEvent(
            MatchingRegisteredChatEvent(
                listOf(helper.daangnId),
                "$baseUrl/errands/${errand.id}"
            )
        )
        eventScheduler.addElement(
            MatchingAfterChatEvent(listOf(helper.daangnId), "$baseUrl/errands/${errand.id}"),
            LocalDateTime.now().plusHours(24)
        )
    }

    fun readMain(userId: Long, lastId: Long?, size: Long, regionId: String): List<ErrandPreview> {
        val neighbors = daangnUtil.getNeighborRegionByRegionId(regionId).data.region.neighborRegions
        val neighborIds = Region.convertRegionListToRegionIdList(neighbors)
        val errands =
            if (lastId == null) {
                errandRepository.findErrandOrderByCreatedAtDesc(size, neighborIds)
            } else {
                val lastErrand = errandRepository.findById(lastId)
                    .orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
                errandRepository.findErrandsAfterLastErrandOrderByCreatedAtDesc(lastErrand, size, neighborIds)
            }
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        return errands.asSequence().map { errand ->
            val errandPreview = errandConverter.toErrandPreview(errand)
            errandPreview.helpCount = helpRepository.countByErrand(errand)
            errandPreview.thumbnailUrl = if (errand.images.isNotEmpty()) errand.images[0].url else null
            val didUserApplyButWasChosen =
                (errand.chosenHelper != user) && (helpRepository.findByErrandAndHelper(errand, user) != null)
            errandPreview.setStatus(errand, didUserApplyButWasChosen)
            errandPreview.regionName = daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
            errandPreview
        }.toList()
    }

    fun readMainOnlyAppliable(userId: Long, lastId: Long?, size: Long, regionId: String): List<ErrandPreview> {
        val neighborIds =
            Region.convertRegionListToRegionIdList(daangnUtil.getNeighborRegionByRegionId(regionId).data.region.neighborRegions)
        val errands =
            if (lastId == null) {
                errandRepository.findErrandsEnableToApply(size, neighborIds)
            } else {
                val lastErrand = errandRepository.findById(lastId)
                    .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
                errandRepository.findErrandsEnableToApplyAfterLastErrand(lastErrand, size, neighborIds)
            }
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        return errands.asSequence().map { errand -> // TODO: 리팩토링.. 드러버
            val errandPreview = errandConverter.toErrandPreview(errand)
            errandPreview.helpCount = helpRepository.countByErrand(errand)
            errandPreview.thumbnailUrl = if (errand.images.isNotEmpty()) errand.images[0].url else null
            val didUserApplyButWasChosen =
                (errand.chosenHelper != user) && (helpRepository.findByErrandAndHelper(errand, user) != null)
            errandPreview.setStatus(errand, didUserApplyButWasChosen)
            errandPreview.regionName = daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
            errandPreview
        }.filter { e -> e.status != Status.FAIL.name }.toList()
    }

    fun readMyErrands(userId: Long, lastId: Long?, size: Long): List<ErrandPreview> {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errands = if (lastId == null) {
            errandRepository.findByCustomerOrderByCreateAtDesc(user, size)
        } else {
            val lastErrand = errandRepository.findById(lastId)
                .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 아이디의 심부름이 존재하지 않습니다.") }
            errandRepository.findErrandsAfterLastErrandByCustomerOrderedByCreatedAtDesc(lastErrand, user, size)
        }
        return errands.asSequence().map { errand ->
            val errandPreview = errandConverter.toErrandPreview(errand)
            errandPreview.helpCount = helpRepository.countByErrand(errand)
            errandPreview.thumbnailUrl = if (errand.images.isNotEmpty()) errand.images[0].url else null
            errandPreview.setStatus(errand, false)
            errandPreview.regionName = daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
            errandPreview
        }.toList()
    }

    fun readMyHelps(userId: Long, lastId: Long?, size: Long): List<ErrandPreview> {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val helps = if (lastId == null) {
            helpRepository.findByHelperTopSize(user, size)
        } else {
            val errand =
                errandRepository.findById(lastId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
            val lastHelp = helpRepository.findByErrandAndHelper(errand, user)
                ?: throw ErrandException(ErrandError.ENTITY_NOT_FOUND)
            helpRepository.findByHelper(user, lastHelp, size)
        }
        return helps.asSequence().map { help ->
            println(help.errand)
            val errandProfile = errandConverter.toErrandPreview(help.errand)

            errandProfile.setStatus(help.errand, help.errand.chosenHelper != user)
            errandProfile.regionName = daangnUtil.getRegionInfoByRegionId(help.errand.regionId).region.name
            errandProfile
        }.toList()
    }

    fun confirmErrand(userId: Long, errandId: Long) {
        val helper = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        if (helper != errand.chosenHelper) throw ErrandException(ErrandError.NOT_PERMITTED)
        errand.complete = true
        mixpanelEventPublisher.publishErrandCompletedEvent(errand.id!!)
    }

    fun readHelperDetail(payload: JwtPayload, helpId: Long): GetHelpDetailResDto {
        val userId = payload.userId
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val help = helpRepository.findById(helpId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        if (user != help.errand.customer && user != help.helper) throw ErrandException(ErrandError.NOT_PERMITTED)
        val helperVo = UserProfileVo(
            help.helper.id,
            help.helper.daangnId,
            mannerTemp = help.helper.mannerTemp
        )
        return GetHelpDetailResDto(
            help.errand.chosenHelper == help.helper,
            daangnUtil.setUserDetailProfile(helperVo, payload.accessToken, help.regionId),
            help.appeal,
            help.phoneNumber
        )
    }

    fun destroyErrand(userId: Long, errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        errandRepository.delete(errand)
    }
}