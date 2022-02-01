package com.daangn.errand.util

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisUtilImpl(
    val redisTemplate: RedisTemplate<String, String>
): RedisUtil {

    override fun getDaangnIdListByRegionId(regionId: String): List<String> {
        val allKeys = redisTemplate.keys("$regionId*")
        return allKeys.asSequence().map { key -> key.split(":")[1] }.toList()
    }

    override fun createOrUpdateUserRegion(daangnId: String, regionId: String) {
        val allKeys = redisTemplate.keys("*:$daangnId")
        redisTemplate.delete(allKeys)
        redisTemplate.opsForValue().set("$regionId:$daangnId", "", Duration.ofDays(7))
    }
}