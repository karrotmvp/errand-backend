package com.daangn.errand.service

import com.daangn.errand.domain.HelperHasCategories
import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserProfileVo
import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.repository.CategoryRepository
import com.daangn.errand.repository.HelperHasCategoriesRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.CategoryStatus
import com.daangn.errand.rest.dto.GetUserAlarmResDto
import com.daangn.errand.rest.dto.daangn.GetUserProfileRes
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val userConverter: UserConverter,
    private val categoryRepository: CategoryRepository,
    private val helperHasCategoriesRepository: HelperHasCategoriesRepository,
    private val daangnUtil: DaangnUtil,
    private val redisUtil: RedisUtil,
    private val mixpanelEventPublisher: MixpanelEventPublisher,
) {
    fun loginOrSignup(userProfile: GetUserProfileRes.Data, accessToken: String): UserVo {
        val daangnId = userProfile.userId
        var isSignUp = false
        val user: User = userRepository.findByDaangnId(daangnId) ?: run {
            isSignUp = true
            userRepository.save(User(daangnId))
        }
        val mannerTemp: Float = daangnUtil.getMannerTemp(accessToken).mannerPoint + 36.5f
        user.mannerTemp = mannerTemp

        mixpanelEventPublisher.publishErrandSignInEvent(
            user.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE),
            isSignUp
        )
        return userConverter.toUserVo(user)
    }


    fun saveLastRegionId(daangnId: String, regionId: String) {
        redisUtil.createOrUpdateUserRegion(daangnId, regionId)
    }

    fun setCategory(userId: Long, categoryId: Long) {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category =
            categoryRepository.findById(categoryId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        helperHasCategoriesRepository.findByUserAndCategory(user, category)
            ?.let { throw ErrandException(ErrandError.BAD_REQUEST) }
        helperHasCategoriesRepository.save(HelperHasCategories(user, category))
    }

    fun deactivateCategory(userId: Long, categoryId: Long) {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category =
            categoryRepository.findById(categoryId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val helperCategory = helperHasCategoriesRepository.findByUserAndCategory(user, category)
            ?: throw ErrandException(ErrandError.BAD_REQUEST)
        helperHasCategoriesRepository.delete(helperCategory)
    }

    fun getMyProfileDaangnInfo(userId: Long, accessToken: String, regionId: String): UserProfileVo {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val userProfileVo = userConverter.toUserProfileVo(user)
        return daangnUtil.setMyDaangnProfile(userProfileVo, accessToken, regionId)
    }

    fun getUserProfileWithDaangnInfo(userId: Long): UserProfileVo {
        val user = userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val userProfileVo = userConverter.toUserProfileVo(user)
        return daangnUtil.setUserDaangnProfile(userProfileVo)
    }

    fun updateUserAlarm(userId: Long, on: Boolean): String {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (user.isAlarmOn == on) throw ErrandException(ErrandError.BAD_REQUEST, "중복 요청입니다.")
        user.isAlarmOn = on
        return if (user.isAlarmOn) "알림 ON 완료" else "알림 OFF 완료"
    }

    fun readUserAlarm(userId: Long): GetUserAlarmResDto {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val allCategories = categoryRepository.findAll()
        return GetUserAlarmResDto(allCategories.asSequence().map { category ->
            CategoryStatus(
                category.id!!,
                category.name,
                helperHasCategoriesRepository.findByUserAndCategory(user, category) != null
            )
        }.toList(), user.isAlarmOn)
    }
}