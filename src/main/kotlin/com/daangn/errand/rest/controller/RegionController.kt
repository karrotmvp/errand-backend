package com.daangn.errand.rest.controller

import com.daangn.errand.service.RegionService
import com.daangn.errand.support.response.ErrandResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/region")
@Api(tags = ["지역 관련 API"])
class RegionController(val regionService: RegionService) {
    @GetMapping("")
    @ApiOperation(value = "지역정보 가져오기 API")
    fun getRegionInfo(
        @ApiParam(value = "지역 ID") @RequestParam(value = "regionId") regionId: String
    ) = ErrandResponse(regionService.getRegionByDaangnApi(regionId))
}