package com.daangn.errand.service

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.repository.CategoryRepository
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.dto.errand.PostErrandResDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import org.springframework.stereotype.Service

@Service
class ErrandService(
    val userRepository: UserRepository,
    val errandRepository: ErrandRepository,
    val categoryRepository: CategoryRepository
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
        return PostErrandResDto(errand.id?: throw ErrandException(ErrandError.FAIL_TO_CREATE))
    }
}