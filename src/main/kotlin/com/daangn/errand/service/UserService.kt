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
        val pair = createOrUpdateUserByDaangnId(userProfile)
        val isSignUp = pair.first
        val user = pair.second

        mixpanelEventPublisher.publishErrandSignInEvent(
            user.id ?: throw ErrandException(ErrandError.FAIL_TO_CREATE),
            isSignUp
        )
        return userConverter.toUserVo(user)
    }

    fun getTotalUserCnt(): Long {
        return userRepository.count()
    }

    fun getAlarmOnUSerCnt(): Long {
        return helperHasCategoriesRepository.countAlarmOnUser()
    }

    @Transactional
    fun createOrUpdateUserByDaangnId(userProfile: GetUserProfileRes.Data): Pair<Boolean, User> {
        val daangnId = userProfile.userId
        var isSignUp = false
        val user = userRepository.findByDaangnId(daangnId) ?: run {
            isSignUp = true
            userRepository.save(User(daangnId))
        }
        val mannerTemp: Float = daangnUtil.getUserProfile(daangnId).data.user.mannerTemperature ?: 36.5f
        user.mannerTemp = mannerTemp
        return Pair(isSignUp, user)
    }

    @Transactional
    fun saveLastRegionId(daangnId: String, regionId: String) {
        redisUtil.createOrUpdateUserRegion(daangnId, regionId)
    }

    @Transactional
    fun setCategory(userId: Long, categoryId: Long) {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category =
            categoryRepository.findById(categoryId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        helperHasCategoriesRepository.findByUserAndCategory(user, category)
            ?.let { throw ErrandException(ErrandError.BAD_REQUEST) }
        helperHasCategoriesRepository.save(HelperHasCategories(user, category))
    }

    @Transactional
    fun deactivateCategory(userId: Long, categoryId: Long) {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val category =
            categoryRepository.findById(categoryId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val helperCategory = helperHasCategoriesRepository.findByUserAndCategory(user, category)
            ?: throw ErrandException(ErrandError.BAD_REQUEST)
        helperHasCategoriesRepository.delete(helperCategory)
    }

    @Transactional
    fun getUserProfileWithDaangnInfo(userId: Long): UserProfileVo {
        val user = userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val userProfileVo = userConverter.toUserProfileVo(user)
        return daangnUtil.setUserDaangnProfile(userProfileVo)
    }

    @Transactional
    fun getMyProfileWithDaangnInfo(userId: Long, regionId: String): UserProfileVo {
        val user = userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val userProfileVo = daangnUtil.setUserDaangnProfile(userConverter.toUserProfileVo(user))
        userProfileVo.regionName = daangnUtil.getRegionInfoByRegionId(regionId).region.name
        return userProfileVo
    }

    @Transactional
    fun updateUserAlarm(userId: Long, on: Boolean): String {
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (user.isAlarmOn == on) throw ErrandException(ErrandError.BAD_REQUEST, "?????? ???????????????.")
        user.isAlarmOn = on
        return if (user.isAlarmOn) "?????? ON ??????" else "?????? OFF ??????"
    }

    @Transactional
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