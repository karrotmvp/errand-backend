package com.daangn.errand.service

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.errand.ErrandPreview
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
import com.daangn.errand.support.event.ErrandRegisteredEvent
import com.daangn.errand.support.event.MatchingAfterEvent
import com.daangn.errand.support.event.MatchingRegisteredEvent
import com.daangn.errand.support.event.scheduler.EventScheduler
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ErrandService(
    val userRepository: UserRepository,
    val errandRepository: ErrandRepository,
    val errandConverter: ErrandConverter,
    val categoryRepository: CategoryRepository,
    val helperHasCategoriesRepository: HelperHasCategoriesRepository,
    val helpRepository: HelpRepository,
    val regionConverter: RegionConverter,
    val daangnUtil: DaangnUtil,
    val userConverter: UserConverter,
    val eventPublisher: ApplicationEventPublisher,
    @Value("\${host.url}") val baseUrl: String,
    val imageRepository: ImageRepository,
    val eventScheduler: EventScheduler
) {
    fun createErrand(userId: Long, postErrandReqDto: PostErrandReqDto): PostErrandResDto {
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category = categoryRepository.findById(postErrandReqDto.categoryId).orElseThrow {
            throw ErrandException(ErrandError.BAD_REQUEST)
        }
        if (postErrandReqDto.imageUrls.size > 5) throw ErrandException(ErrandError.BAD_REQUEST, "사진은 최대 5장 첨부 가능합니다.")
        val errand = errandRepository.save(
            Errand(
                category = category,
                title = postErrandReqDto.title,
                detail = postErrandReqDto.detail,
                reward = postErrandReqDto.reward,
                detailAddress = postErrandReqDto.detailAddress,
                customerPhoneNumber = postErrandReqDto.phoneNumber,
                customer = user,
                regionId = postErrandReqDto.regionId
            )
        )
        postErrandReqDto.imageUrls.forEach { imageUrl -> imageRepository.save(Image(imageUrl, errand)) }
        val errandId = errand.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE)
        val res = PostErrandResDto(errandId)
        val list = getUserDaangnIdListInCategory(errand)
        val linkUrl = "$baseUrl/errands/$errandId"
        eventPublisher.publishEvent(ErrandRegisteredEvent(list, linkUrl))
        return res
    }

    fun getUserDaangnIdListInCategory(errand: Errand): List<String> {
        val helperHasCategories = helperHasCategoriesRepository.findByCategory(errand.category)
        return helperHasCategories.asSequence().map { row -> row.user.daangnId }.toList()
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
            .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 id의 심부름을 찾d을 수 없습니다.") }
        if (errand.chosenHelper != null) throw ErrandException(ErrandError.BAD_REQUEST, "이미 지정된 헬퍼가 있습니다.")
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)

        val helper = userRepository.findById(helperId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.chosenHelper = helper

        eventPublisher.publishEvent(MatchingRegisteredEvent(listOf(helper.daangnId), "$baseUrl/errands/${errand.id}"))
        eventScheduler.addElement(
            MatchingAfterEvent(listOf(helper.daangnId), "$baseUrl/errands/${errand.id}"),
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
}