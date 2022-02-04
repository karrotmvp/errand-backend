package com.daangn.errand.service

import com.daangn.errand.service.daangn.DaangnOpenApiService
import com.daangn.errand.service.daangn.dto.RegionConverter
import com.daangn.errand.service.daangn.dto.RegionVo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegionService(val daangnOpenAPIService: DaangnOpenApiService, val regionConverter: RegionConverter) {
    fun getRegionByDaangnApi(regionId: String): RegionVo {
        val resData = daangnOpenAPIService.getRegionInfoByRegionId(regionId)
        return regionConverter.toRegionVo(resData.region)
    }
}
