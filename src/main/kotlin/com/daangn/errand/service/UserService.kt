package com.daangn.errand.service

import com.daangn.errand.domain.HelperHasCategories
import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.repository.CategoryRepository
import com.daangn.errand.repository.HelperHasCategoriesRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.daangn.GetUserProfileRes
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    val userRepository: UserRepository,
    val userConverter: UserConverter,
    val categoryRepository: CategoryRepository,
    val helperHasCategoriesRepository: HelperHasCategoriesRepository
) {
    fun loginOrSignup(userProfile: GetUserProfileRes.Data): UserVo {
        val daangnId = userProfile.userId
        val user = userRepository.findByDaangnId(daangnId) ?: userRepository.save(User(daangnId))
        return userConverter.toUserVo(user)
    }

    fun setCategory(userId: Long, categoryId: Long) { // TODO: category 설정 api 하고 테스트
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category =
            categoryRepository.findById(categoryId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        helperHasCategoriesRepository.findByUserAndCategory(user, category)
            ?.let { throw ErrandException(ErrandError.BAD_REQUEST) }
        helperHasCategoriesRepository.save(HelperHasCategories(user, category))
    }

    fun deactivateCategory(userId: Long, categoryId: Long) { // TODO: 테스트
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category =
            categoryRepository.findById(categoryId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val helperCategory = helperHasCategoriesRepository.findByUserAndCategory(user, category)
            ?: throw ErrandException(ErrandError.BAD_REQUEST)
        helperHasCategoriesRepository.delete(helperCategory)
    }
}