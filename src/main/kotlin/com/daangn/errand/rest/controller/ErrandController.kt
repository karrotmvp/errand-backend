package com.daangn.errand.rest.controller

import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
@Api(tags = ["심부름 관련 API"])
@RequestMapping("/errand")
class ErrandController(
    val errandService: ErrandService
) {
    @PostMapping("")
    @ApiOperation(value = "심부름을 등록하는 API")
    fun postErrand(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @RequestBody postErrandReqDto: PostErrandReqDto
    ) = ErrandResponse(errandService.createErrand(payload.userId, postErrandReqDto))
}