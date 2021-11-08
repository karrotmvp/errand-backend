package com.daangn.errand.service

import com.daangn.errand.domain.errand.*
import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.repository.*
import com.daangn.errand.rest.dto.daangn.Region
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.rest.dto.help.GetHelpDetailResDto
import com.daangn.errand.rest.dto.help.HelperPreview
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.publisher.DaangnChatEventPublisher
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.S3Uploader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ErrandService(
    private val userRepository: UserRepository,
    private val errandRepository: ErrandRepository,
    private val categoryRepository: CategoryRepository,
    private val helpRepository: HelpRepository,
    private val imageRepository: ImageRepository,

    private val errandConverter: ErrandConverter,
    private val regionConverter: RegionConverter,
    private val userConverter: UserConverter,
    private val s3Uploader: S3Uploader,

    private val daangnUtil: DaangnUtil,
    private val daangnChatEventPublisher: DaangnChatEventPublisher,
    private val mixpanelEventPublisher: MixpanelEventPublisher,
) {
    fun createErrand(userId: Long, postErrandReqDto: PostErrandReqDto): PostErrandResDto {
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category = categoryRepository.findById(postErrandReqDto.categoryId).orElseThrow {
            throw ErrandException(ErrandError.BAD_REQUEST)
        }
        if (!postErrandReqDto.images.isNullOrEmpty() && postErrandReqDto.images.size > 5) throw ErrandException(
            ErrandError.BAD_REQUEST,
            "사진은 최대 5장까지만 첨부 가능합니다."
        )
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
        postErrandReqDto.images?.forEach { image ->
            val fileName = "${errand.id}-${user.id}-${LocalDateTime.now()}"
            val imageUrl = s3Uploader.upload(image, fileName, "errand/img")
            imageRepository.save(Image(imageUrl, errand))
        }
        val errandId = errand.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE)

        daangnChatEventPublisher.publishErrandRegisteredEvent(errandId)
        mixpanelEventPublisher.publishErrandRegisteredEvent(errandId)

        return PostErrandResDto(errandId)
    }

    fun readErrand(payload: JwtPayload, errandId: Long): GetErrandResDto<ErrandDto> {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val user =
            userRepository.findById(payload.userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errandDto = makeErrandToErrandDto(errand)
        errand.viewCnt += 1
        return getErrandDetailByUserRole(errand, user, errandDto)
    }

    private fun makeErrandToErrandDto(errand: Errand): ErrandDto {
        val errandDto = errandConverter.toErrandDto(errand)
        errandDto.region = regionConverter.toRegionVo(daangnUtil.getRegionInfoByRegionId(errand.regionId).region)
        errandDto.helpCount = helpRepository.countByErrand(errand)
        return errandDto
    }

    private fun getErrandDetailByUserRole(
        errand: Errand,
        user: User,
        errandDto: ErrandDto
    ): GetErrandResDto<ErrandDto> {
        val isMine = errand.customer == user
        val didIApply: Boolean = !isMine && helpRepository.findByErrandAndHelper(errand, user) != null
        val wasIChosen = errand.chosenHelper == user

        errandDto.setStatus(errand, didIApply && !wasIChosen)
        if (!isMine && errand.complete || !isMine && !wasIChosen) {
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

    fun readAppliedHelpers(payload: JwtPayload, errandId: Long): List<HelperPreview> {
        val errand = errandRepository.findById(errandId)
            .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 아이디의 심부름이 존재하지 않습니다.") }
        if (errand.complete) throw ErrandException(ErrandError.NOT_PERMITTED, "완료된 심부름의 지원자 목록은 볼 수 없어요.")

        val user =
            userRepository.findById(payload.userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)

        return convertHelpListToHelpPreviewList(errand)
    }

    private fun convertHelpListToHelpPreviewList(errand: Errand): List<HelperPreview> {
        return helpRepository.findByErrandOrderByCreatedAt(errand).asSequence().map { help ->
            val userProfileVo =
                daangnUtil.setUserDaangnProfile(
                    userConverter.toUserProfileVo(help.helper),
                    help.regionId
                )
            HelperPreview(
                help.id!!,
                userProfileVo,
                help.appeal
            )
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

        daangnChatEventPublisher.publishMatchingRegisteredEvent(helper.daangnId, errandId)
        daangnChatEventPublisher.publishMatchingAfterChatEvent(helper.daangnId, errandId)
    }

    fun readMain(userId: Long, lastId: Long?, size: Long, regionId: String): List<GetErrandResDto<ErrandPreview>> {
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
        return makeErrandPreviewByUserRole(errands, user)
    }

    private fun makeErrandPreviewByUserRole(
        errands: MutableList<Errand>,
        user: User
    ): List<GetErrandResDto<ErrandPreview>> {
        return errands.asSequence().map { errand ->
            val isMine = errand.customer == user
            val didIApply: Boolean = !isMine && helpRepository.findByErrandAndHelper(errand, user) != null
            val wasIChosen = errand.chosenHelper == user
            GetErrandResDto(makeErrandToErrandPreview(errand, user), isMine, didIApply, wasIChosen)
        }.toList()
    }

    private fun makeErrandToErrandPreview(
        errand: Errand,
        user: User
    ): ErrandPreview {
        val errandPreview = errandConverter.toErrandPreview(errand)
        errandPreview.helpCount = helpRepository.countByErrand(errand)
        errandPreview.thumbnailUrl = if (errand.images.isNotEmpty()) errand.images[0].url else null
        val didUserApplyButWasChosen =
            (errand.chosenHelper != user) && (helpRepository.findByErrandAndHelper(errand, user) != null)
        errandPreview.setStatus(errand, didUserApplyButWasChosen)
        errandPreview.regionName = daangnUtil.getRegionInfoByRegionId(errand.regionId).region.name
        return errandPreview
    }

    fun readMainOnlyAppliable(
        userId: Long,
        lastId: Long?,
        size: Long,
        regionId: String
    ): List<GetErrandResDto<ErrandPreview>> {
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
        return makeErrandPreviewByUserRole(errands, user).filter { e -> e.errand.status != Status.FAIL.name }
    }

    fun readMyErrands(userId: Long, lastId: Long?, size: Long): List<GetErrandResDto<ErrandPreview>> {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errands = if (lastId == null) {
            errandRepository.findByCustomerOrderByCreateAtDesc(user, size)
        } else {
            val lastErrand = errandRepository.findById(lastId)
                .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 아이디의 심부름이 존재하지 않습니다.") }
            errandRepository.findErrandsAfterLastErrandByCustomerOrderedByCreatedAtDesc(lastErrand, user, size)
        }
        return makeErrandPreviewByUserRole(errands, user)
    }

    fun readMyHelps(userId: Long, lastId: Long?, size: Long): List<GetErrandResDto<ErrandPreview>> {
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
        val errands = helps.asSequence().map { help -> help.errand }.toMutableList()
        return makeErrandPreviewByUserRole(errands, user)
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

        val isHelper = user == help.helper
        val isCustomer = user == help.errand.customer

        if (!isCustomer && !isHelper) throw ErrandException(ErrandError.NOT_PERMITTED)
        val helperVo = UserProfileVo(
            help.helper.id,
            help.helper.daangnId,
            mannerTemp = help.helper.mannerTemp
        )

        val isChosenHelper = help.errand.chosenHelper == help.helper
        return GetHelpDetailResDto(
            help.errand.chosenHelper == help.helper,
            daangnUtil.setUserDaangnProfile(helperVo, help.regionId),
            help.appeal,
            if (isHelper || (isCustomer && isChosenHelper)) help.phoneNumber else null
        )
    }

    fun destroyErrand(userId: Long, errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        errandRepository.delete(errand)
    }
}