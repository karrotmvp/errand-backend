package com.daangn.errand.service

import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.rest.dto.daangn.RegionVo
import com.daangn.errand.util.DaangnUtil
import org.springframework.stereotype.Service

@Service
class RegionService(val daangnUtil: DaangnUtil, val regionConverter: RegionConverter) {
    fun getRegionByDaangnApi(regionId: String): RegionVo {
        val resData = daangnUtil.getRegionInfoByRegionId(regionId)
        return regionConverter.toRegionVo(resData.region)
    }
}