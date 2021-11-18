package com.daangn.errand.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate

@SpringBootTest
@EnabledIfEnvironmentVariable(
    named="SPRING_PROFILES_ACTIVE",
    matches="local"
)
internal class RedisUtilTest constructor(
    @Autowired val redisTemplate: RedisTemplate<String, String>,
    @Autowired val redisUtil: RedisUtil
) {
    @Test
    fun createUserRegion() {
        // given
        val daangnId = "8a190fa9bb5d4d89b3944dc8c5b3a102test"
        val regionId = "6530459d189b"

        // when
        redisUtil.createOrUpdateUserRegion(daangnId, regionId)

        //then
        val userRegion = redisTemplate.keys("*:$daangnId")
        Assertions.assertThat(userRegion.size).isEqualTo(1)

        redisTemplate.delete("$regionId:$daangnId")
    }

    @Test
    fun getUsersByRegionId() {
        // given
        val user1Id = "8a190fa9bb5d4d89b3944dc8c5b3a102test"
        val user2Id = "8a190fa9bb5d4d89b3944dc8c5b3a103test"
        val regionId = "6530459d189b"
        redisTemplate.opsForValue().set("$regionId:$user1Id", "")
        redisTemplate.opsForValue().set("$regionId:$user2Id", "")

        // when
        val userRegions = redisUtil.getDaangnIdListByRegionId("6530459d189b")

        // then
        Assertions.assertThat(userRegions.size).isEqualTo(2)

        redisTemplate.delete(redisTemplate.keys("*test"))
    }
}