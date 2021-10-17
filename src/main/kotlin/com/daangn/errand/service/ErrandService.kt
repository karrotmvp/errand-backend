package com.daangn.errand.service

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.repository.CategoryRepository
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import org.springframework.stereotype.Service

@Service
class ErrandService(
    val userRepository: UserRepository,
    val errandRepository: ErrandRepository,
    val errandConverter: ErrandConverter,
    val categoryRepository: CategoryRepository,
    val helpRepository: HelpRepository,
    val daangnUtil: DaangnUtil
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
        errandDto.region = daangnUtil.getRegionInfoByRegionId(errand.regionId)
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
}