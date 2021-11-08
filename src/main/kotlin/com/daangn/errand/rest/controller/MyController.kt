package com.daangn.errand.rest.controller

import com.daangn.errand.domain.errand.ErrandPreview
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/my")
@Api(tags = ["마이페이지 관련 API"])
class MyController(
    val errandService: ErrandService
) {
    @GetMapping("/errands")
    @ApiOperation("내가 작성한 심부름 가져오기 API")
    fun getMyErrands(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "마지막 심부름 아이디", required = false) @RequestParam(value = "lastId") lastId: Long?,
        @ApiParam(value = "가져오려는 심부름의 개수") @RequestParam(value = "size") size: Long
    ): ErrandResponse<List<GetErrandResDto<ErrandPreview>>> {
        return ErrandResponse(errandService.readMyErrands(payload.userId, lastId, size))
    }

    @GetMapping("/helps")
    @ApiOperation(value = "내가 지원한 심부름 가져오기 API")
    fun getMyHelps(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "마지막 심부름 아이디", required = false) @RequestParam(value = "lastId") lastId: Long?,
        @ApiParam(value = "가져오려는 심부름의 개수") @RequestParam(value = "size") size: Long
    ) = ErrandResponse(errandService.readMyHelps(payload.userId, lastId, size))
}