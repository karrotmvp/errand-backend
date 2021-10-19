package com.daangn.errand.rest.controller

import com.daangn.errand.domain.errand.ErrandPreview
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/my")
class MyController(
    val errandService: ErrandService
) {
    @GetMapping("/errands")
    fun getMyErrands(
        @TokenPayload payload: JwtPayload,
        @RequestParam(value = "lastId") lastId: Long?,
        @RequestParam(value = "size") size: Long
    ): ErrandResponse<List<ErrandPreview>> {
        return ErrandResponse(errandService.readMyErrands(payload.userId, lastId, size))
    }
}