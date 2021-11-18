package com.daangn.errand.domain.user

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
internal class UserTest constructor(
    @Autowired val userConverter: UserConverter
) {
    @Test
    fun `user converter 테스트`() {
        val user = User(
            "mockDaangnId",
        )
        val userVo = userConverter.toUserVo(user)
        Assertions.assertThat(userVo).isInstanceOf(UserVo::class.java)
        Assertions.assertThat(user.daangnId).isEqualTo("mockDaangnId")
    }
}