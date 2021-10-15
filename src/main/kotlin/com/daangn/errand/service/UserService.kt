package com.daangn.errand.service

import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.daangn.GetUserProfileRes
import com.daangn.errand.util.DaangnUtil
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val userConverter: UserConverter
) {
    fun loginOrSignup(userProfile: GetUserProfileRes.Data): UserVo {
        val daangnId = userProfile.userId
        val user = userRepository.findByDaangnId(daangnId) ?: userRepository.save(User(daangnId))
        return userConverter.toUserVo(user)
    }
}