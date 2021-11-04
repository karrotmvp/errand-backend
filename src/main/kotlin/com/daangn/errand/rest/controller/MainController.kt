package com.daangn.errand.rest.controller

import com.daangn.errand.domain.errand.ErrandPreview
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/errands")
class MainController(
    val errandService: ErrandService
) {
    @GetMapping("")
    fun getMain(
        @TokenPayload payload: JwtPayload,
        @RequestParam(value = "lastId") lastId: Long?,
        @RequestParam(value = "size") size: Long,
        @RequestParam(value = "regionId") regionId: String
    ): ErrandResponse<List<GetErrandResDto<ErrandPreview>>> {
        return ErrandResponse(errandService.readMain(payload.userId, lastId, size, regionId))
    }

    @GetMapping("/appliable")
    fun getMainOnlyAppliable(
        @TokenPayload payload: JwtPayload,
        @RequestParam(value = "lastId") lastId: Long?,
        @RequestParam(value = "size") size: Long,
        @RequestParam(value = "regionId") regionId: String
    ): ErrandResponse<List<GetErrandResDto<ErrandPreview>>> {
        return ErrandResponse(errandService.readMainOnlyAppliable(payload.userId, lastId, size, regionId))
    }
}