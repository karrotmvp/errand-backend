package com.daangn.errand.service

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.errand.ErrandPreview
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.repository.CategoryRepository
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ErrandService(
    val userRepository: UserRepository,
    val errandRepository: ErrandRepository,
    val errandConverter: ErrandConverter,
    val categoryRepository: CategoryRepository,
    val helpRepository: HelpRepository,
    val regionConverter: RegionConverter,
    val daangnUtil: DaangnUtil,
    val userConverter: UserConverter,
) {
    fun createErrand(userId: Long, postErrandReqDto: PostErrandReqDto): PostErrandResDto {
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category = categoryRepository.findById(postErrandReqDto.categoryId).orElseThrow {
            throw ErrandException(ErrandError.BAD_REQUEST)
        }
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
        return PostErrandResDto(errand.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE))
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
                daangnUtil.setUserDetailProfile(userConverter.toUserProfileVo(help.helper), accessToken) // TODO 다시하기
            userProfileVo.regionName = daangnUtil.getRegionInfoByRegionId(help.regionId).region.name
            userProfileVo
        }.toList()
    }

    // TODO: 알림톡
    fun chooseHelper(userId: Long, helperId: Long, errandId: Long) {
        println(errandId)
        val errand = errandRepository.findById(errandId)
            .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST, "해당 id의 심부름을 찾을 수 없습니다.") }
        if (errand.chosenHelper != null) throw ErrandException(ErrandError.BAD_REQUEST, "이미 지정된 헬퍼가 있습니다.")
        val user =
            userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (errand.customer != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        val helper = userRepository.findById(helperId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.chosenHelper = helper
    }

    fun readMain(userId: Long, lastId: Long?, size: Long): List<ErrandPreview> {
        val errands =
            if (lastId == null) {
                errandRepository.findErrandOrderByCreatedAtDesc(size)
            } else {
                val lastErrand = errandRepository.findById(lastId)
                    .orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
                errandRepository.findErrandsAfterLastErrandOrderByCreatedAtDesc(lastErrand, size)
            }
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        return errands.asSequence().map { errand ->
            val errandPreview = errandConverter.toErrandPreview(errand)
            errandPreview.helpCount = helpRepository.countByErrand(errand)
            errandPreview.thumbnailUrl = if (errand.images.isNotEmpty()) errand.images[0].url else null
            val didUserApplyButWasChosen =
                (errand.chosenHelper != user) && (helpRepository.findByErrandAndHelper(errand, user) != null)
            errandPreview.setStatus(errand, didUserApplyButWasChosen)
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
            errandPreview
        }.toList()
    }
}