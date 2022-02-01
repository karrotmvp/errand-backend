package com.daangn.errand.util

interface RedisUtil {
    fun getDaangnIdListByRegionId(regionId: String): List<String>
    fun createOrUpdateUserRegion(daangnId: String, regionId: String)
}