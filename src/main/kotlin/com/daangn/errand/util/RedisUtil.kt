package com.daangn.errand.util

interface RedisUtil {
    fun getDaangnUserIdsBy(regionId: String): List<String>
    fun createOrUpdateUserRegion(daangnId: String, regionId: String)
}
