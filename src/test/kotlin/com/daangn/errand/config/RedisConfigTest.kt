package com.daangn.errand.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile

@SpringBootTest
@Profile("dev")
internal class RedisConfigTest constructor(
    @Autowired private val redisConfig: RedisConfig,
    @Value("\${spring.redis.host}") val hostInApplicationProp: String
){
    @Test
    fun `redis의 주소는 elasticache여야한다`() {
        println(hostInApplicationProp)
        assertThat(redisConfig.host).isEqualTo(hostInApplicationProp)
    }
}