package com.daangn.errand.domain.user

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class UserTest constructor(
    @Autowired val userConverter: UserConverter
) {
    @Test
    fun `user converter 테스트`() {
        val user = User(
            nickname = "Rosie",
            phoneNumber = "01012345678"
        )
        val userVo = userConverter.toUserVo(user)
        Assertions.assertThat(userVo).isInstanceOf(UserVo::class.java)
        Assertions.assertThat(user.nickname).isEqualTo(userVo.nickname)
        Assertions.assertThat(user.phoneNumber).isEqualTo(userVo.phoneNumber)
    }
}