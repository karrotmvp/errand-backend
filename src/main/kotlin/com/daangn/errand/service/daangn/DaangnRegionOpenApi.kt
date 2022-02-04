package com.daangn.errand.service.daangn

import com.daangn.errand.service.daangn.dto.GetNeighborRegionInfoRes
import com.daangn.errand.service.daangn.dto.GetRegionInfoRes

interface DaangnRegionOpenApi {
    fun getRegionInfoByRegionId(regionId: String): GetRegionInfoRes.Data
    fun getNeighborRegionByRegionId(regionId: String): GetNeighborRegionInfoRes
    fun getRegionInfoByRegionIdMap(regionIds: MutableSet<String>): MutableMap<String, String>
}
