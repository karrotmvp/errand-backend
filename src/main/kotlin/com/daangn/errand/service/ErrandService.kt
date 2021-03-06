package com.daangn.errand.service

import com.daangn.errand.domain.errand.*
import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.repository.*
import com.daangn.errand.rest.dto.daangn.Region
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.rest.dto.help.HelperPreview
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.HelperConfirmedErrandEvent
import com.daangn.errand.support.event.publisher.DaangnChatEventPublisher
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ErrandService(
    private val userRepository: UserRepository,
    private val errandRepository: ErrandRepository,
    private val categoryRepository: CategoryRepository,
    private val helpRepository: HelpRepository,
    private val imageRepository: ImageRepository,

    private val errandConverter: ErrandConverter,
    private val regionConverter: RegionConverter,
    private val userConverter: UserConverter,

    private val daangnUtil: DaangnUtil,
    private val daangnChatEventPublisher: DaangnChatEventPublisher,
    private val mixpanelEventPublisher: MixpanelEventPublisher,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun getMatchedErrandRate(): Float {
        val totalErrandCnt = errandRepository.count()
        val matchedErrandCnt = errandRepository.countByChosenHelperIsNotNull()
        return matchedErrandCnt.toFloat() / totalErrandCnt * 100
    }

    fun createErrandAndPublishEvents(userId: Long, postErrandReqDto: PostErrandReqDto): PostErrandResDto {
        val errand = createErrand(userId, postErrandReqDto)

        val errandId = errand.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE)

        val errandDto = errandConverter.toErrandDto(errand)
        daangnChatEventPublisher.publishErrandRegisteredEvent(errandDto)
        mixpanelEventPublisher.publishErrandRegisteredEvent(errandId)

        return PostErrandResDto(errandId)
    }

    @Transactional
    fun createErrand(
        userId: Long,
        postErrandReqDto: PostErrandReqDto
    ): Errand {
        val user =
            userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category = categoryRepository.findById(postErrandReqDto.categoryId).orElseThrow {
            throw ErrandException(ErrandError.BAD_REQUEST)
        }
        if (!postErrandReqDto.images.isNullOrEmpty() && postErrandReqDto.images.size > 10) throw ErrandException(
            ErrandError.BAD_REQUEST,
            "????????? ?????? 10???????????? ?????? ???????????????."
        )
        val errand = errandRepository.save(
            Errand(
                category = category,
                detail = postErrandReqDto.detail,
                reward = postErrandReqDto.reward.toString(),
                detailAddress = postErrandReqDto.detailAddress,
                customerPhoneNumber = postErrandReqDto.phoneNumber,
                customer = user,
                regionId = postErrandReqDto.regionId
            )
        )

        if (!postErrandReqDto.images.isNullOrEmpty()) {
            postErrandReqDto.images.map { imgUrl ->
                imageRepository.save(Image(imgUrl, errand))
            }
        }
        return errand
    }

    @Transactional
    fun readErrand(payload: JwtPayload, errandId: Long): GetErrandResDto<ErrandDto> {
        val errand = findValidErrand(errandId)
        val user =
            userRepository.findById(payload.userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errandDto = makeErrandToErrandDto(errand)
        errand.viewCnt += 1
        return getErrandDetailByUserRole(errand, user, errandDto)
    }

    private fun findValidErrand(errandId: Long): Errand {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        if (errand.unexposed) throw ErrandException(ErrandError.UNEXPOSED_ERRAND)
        return errand
    }

    @Transactional
    fun makeErrandToErrandDto(errand: Errand): ErrandDto {
        val errandDto = errandConverter.toErrandDto(errand)
        errandDto.region = regionConverter.toRegionVo(daangnUtil.getRegionInfoByRegionId(errand.regionId).region)
        errandDto.helpCount = helpRepository.countByErrand(errand)
        daangnUtil.setUserDaangnProfile(errandDto.customer)
        return errandDto
    }

    @Transactional
    fun getErrandDetailByUserRole(
        errand: Errand,
        user: User,
        errandDto: ErrandDto
    ): GetErrandResDto<ErrandDto> {

        val didUserApply: Boolean = helpRepository.existsByErrandAndHelper(errand, user) // helper ??????????????? join ??????
        val isUserCustomer = if (didUserApply) false else errand.customer == user
        val isUserChosenHelper = if (didUserApply && !isUserCustomer) errand.chosenHelper == user else false
        errandDto.setStatus(errand, user, didUserApply)

        if (!isUserCustomer && !isUserChosenHelper || (isUserChosenHelper && errand.complete)) {
            errandDto.customerPhoneNumber = null
            errandDto.detailAddress = null
        }
        var helpId: Long? = null

        val isUserCustomerAndIsErrandMatchedButNotCompleted =
            isUserCustomer && errand.chosenHelper != null && !errand.complete

        if (isUserCustomerAndIsErrandMatchedButNotCompleted) { // ????????? ?????? ??????
            helpId = helpRepository.findByErrandAndHelper(
                errand,
                errand.chosenHelper ?: throw ErrandException(ErrandError.ENTITY_NOT_FOUND)
            )?.id
        }

        if (didUserApply) helpId = helpRepository.findByErrandAndHelper(errand, user)?.id // ???????????? ??? ?????? ????????????

        return GetErrandResDto(
            errand = errandDto,
            isMine = isUserCustomer,
            didIApply = didUserApply,
            wasIChosen = isUserChosenHelper,
            helpId
        )
    }

    @Transactional
    fun readAppliedHelpers(payload: JwtPayload, errandId: Long): List<HelperPreview> {
        val errand = findValidErrand(errandId)
        if (errand.complete) throw ErrandException(ErrandError.NOT_PERMITTED, "????????? ???????????? ????????? ????????? ??? ??? ?????????.")

        val user =
            userRepository.findById(payload.userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)

        return convertHelpListToHelpPreviewList(errand)
    }

    @Transactional
    fun convertHelpListToHelpPreviewList(errand: Errand): List<HelperPreview> {
        return helpRepository.findByErrandOrderByCreatedAt(errand).asSequence().map { help ->
            val userProfileVo =
                daangnUtil.setUserDaangnProfile(
                    userConverter.toUserProfileVo(help.helper),
                    help.regionId
                )
            HelperPreview(
                errand.id!!,
                help.id!!,
                userProfileVo,
                help.appeal,
                help.createdAt
            )
        }.toList()
    }

    @Transactional
    fun chooseHelper(userId: Long, helperId: Long, errandId: Long) {
        val errand = findValidErrand(errandId)
        if (errand.chosenHelper != null) throw ErrandException(ErrandError.BAD_REQUEST, "?????? ????????? ????????? ????????????.")
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        val helper = userRepository.findById(helperId).orElseThrow { ErrandException(ErrandError.BAD_REQUEST) }
        val help =
            helpRepository.findByErrandAndHelper(errand, helper) ?: throw ErrandException(ErrandError.BAD_REQUEST)
        errand.chosenHelper = help.helper

        daangnChatEventPublisher.publishMatchingRegisteredEvent(helper.daangnId, errandId)
        daangnChatEventPublisher.publishMakeCompleteNotiEntityEvent(errandId)
    }

    @Transactional
    fun readMain(userId: Long, lastId: Long?, size: Long, regionId: String): List<GetErrandResDto<ErrandPreview>> {
        val neighbors = daangnUtil.getNeighborRegionByRegionId(regionId).data.region.neighborRegions
        val neighborIds = Region.convertRegionListToRegionIdList(neighbors)
        val errands = errandRepository.findMainErrands(userId, size, neighborIds, lastId)
        return makeErrandPreviewByUserRole(errands, userId)
    }

    @Transactional
    fun makeErrandPreviewByUserRole(
        errands: MutableList<MainErrandQueryResult>,
        userId: Long
    ): List<GetErrandResDto<ErrandPreview>> {
        val regionIds: MutableSet<String> = HashSet()
        errands.forEach { e -> regionIds.add(e.regionId) }
        val regionIdAndNameHashMap = daangnUtil.getRegionInfoByRegionIdMap(regionIds)
        return errands.asSequence().map { errand ->
            errand.helpCount = helpRepository.countByErrandId(errand.id)
            val isMine = errand.customerId == userId
            val didIApply: Boolean = !isMine && errand.viewerHelpId != null
            val wasIChosen = errand.chosenHelperId == userId
            val regionName = regionIdAndNameHashMap[errand.regionId] as String
            GetErrandResDto(makeErrandToErrandPreview(errand, userId, regionName), isMine, didIApply, wasIChosen)
        }.toList()
    }

    /* TODO: ?????? ????????? MainErrandQueryResult ??? ???????????? ?????? ????????? ?????????. */
    @Transactional
    fun makeErrandPreviewByUserRole(
        errands: MutableList<Errand>,
        user: User
    ): List<GetErrandResDto<ErrandPreview>> {
        val regionIds: MutableSet<String> = HashSet()
        errands.forEach { e -> regionIds.add(e.regionId) }
        val regionIdAndNameHashMap = daangnUtil.getRegionInfoByRegionIdMap(regionIds)
        return errands.asSequence().map { errand ->
            val isMine = errand.customer == user
            val didIApply: Boolean = !isMine && helpRepository.existsByErrandAndHelper(errand, user)
            val wasIChosen = errand.chosenHelper == user
            val regionName = regionIdAndNameHashMap[errand.regionId] as String
            GetErrandResDto(makeErrandToErrandPreview(errand, user, regionName), isMine, didIApply, wasIChosen)
        }.toList()
    }

    @Transactional
    fun makeErrandToErrandPreview(
        errand: MainErrandQueryResult,
        userId: Long,
        regionName: String,
    ): ErrandPreview {
        val errandPreview = errandConverter.toErrandPreview(errand)
        errandPreview.thumbnailUrl = imageRepository.findOneByErrandIdOrderByIdDesc(errand.id)?.url
        errandPreview.setStatus(errand, userId)
        errandPreview.regionName = regionName
        return errandPreview
    }

    @Transactional
    fun readMainOnlyAppliable(
        userId: Long,
        lastId: Long?,
        size: Long,
        regionId: String
    ): List<GetErrandResDto<ErrandPreview>> {
        val neighborIds =
            Region.convertRegionListToRegionIdList(daangnUtil.getNeighborRegionByRegionId(regionId).data.region.neighborRegions)
        val errands =
            errandRepository.findAppliableMainErrands(lastId, userId, neighborIds, size)
        return makeErrandPreviewByUserRole(errands, userId)
//            .filter { e -> e.errand.status != Status.FAIL.name } // ????????? chosenHelper ??? ?????? ???????????? ???????????? ????????? fail ?????????.
    }

    @Transactional
    fun makeErrandToErrandPreview(
        errand: Errand,
        user: User,
        regionName: String,
    ): ErrandPreview {
        val errandPreview = errandConverter.toErrandPreview(errand)
        errandPreview.helpCount = helpRepository.countByErrand(errand)
        errandPreview.thumbnailUrl = if (imageRepository.existsByErrand(errand)) errand.images[0].url else null
        errandPreview.setStatus(errand, user, helpRepository.existsByErrandAndHelper(errand, user))
        errandPreview.regionName = regionName
        return errandPreview
    }

    @Transactional
    fun readMyErrands(userId: Long, lastId: Long?, size: Long): List<GetErrandResDto<ErrandPreview>> {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errands = if (lastId == null) {
            errandRepository.findByCustomerOrderByCreateAtDesc(null, user, size)
        } else {
            val lastErrand = errandRepository.findById(lastId)
                .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "?????? ???????????? ???????????? ???????????? ????????????.") }
            errandRepository.findByCustomerOrderByCreateAtDesc(lastErrand, user, size)
        }
        return makeErrandPreviewByUserRole(errands, user)
    }

    @Transactional
    fun readHelpedErrands(userId: Long, lastId: Long?, size: Long): List<GetErrandResDto<ErrandPreview>> {
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
        val errands =
            helps.asSequence().map { help -> help.errand }.filter { errand -> errand.unexposed.not() }.toMutableList()
        return makeErrandPreviewByUserRole(errands, user)
    }

    @Transactional
    fun confirmErrand(userId: Long, errandId: Long) {
        val helper = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errand = findValidErrand(errandId)
        if (helper != errand.chosenHelper) throw ErrandException(ErrandError.NOT_PERMITTED)
        errand.complete = true
        mixpanelEventPublisher.publishErrandCompletedEvent(errand.id!!)
        eventPublisher.publishEvent(HelperConfirmedErrandEvent(errand.id!!))
    }

    @Transactional
    fun destroyErrand(userId: Long, errandId: Long) {
        val errand = findValidErrand(errandId)
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        errandRepository.delete(errand)
    }
}