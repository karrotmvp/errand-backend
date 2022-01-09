package com.daangn.errand.util.daangnUtil

import com.daangn.errand.rest.dto.daangn.GetNeighborRegionInfoRes
import com.daangn.errand.rest.dto.daangn.GetRegionInfoRes

interface DaangnRegionUtil {
    fun getRegionInfoByRegionId(regionId: String): GetRegionInfoRes.Data
    fun getNeighborRegionByRegionId(regionId: String): GetNeighborRegionInfoRes
    fun getRegionInfoByRegionIdMap(regionIds: MutableSet<String>): MutableMap<String, String>
}