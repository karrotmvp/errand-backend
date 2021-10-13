package com.daangn.errand.service

import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.domain.user.UserVo
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.daangn.GetUserProfileRes
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val userConverter: UserConverter
) {
    fun loginOrSignup(userInfo: GetUserProfileRes.Data): UserVo {
        val daangnId = userInfo.userId
        val user = userRepository.findById(daangnId).orElse(
            userRepository.save(User(daangnId))
        )
        //TODO: jwt 해라
        return userConverter.toUserVo(user)
    }
}