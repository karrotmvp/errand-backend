package com.daangn.errand.rest.controller

import com.daangn.errand.service.RegionService
import com.daangn.errand.support.response.ErrandResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/region")
class RegionController(val regionService: RegionService) {
    @GetMapping("")
    fun getRegionInfo(
        @RequestParam(value = "regionId") regionId: String
    ) = ErrandResponse(regionService.getRegionByDaangnApi(regionId))
}