package com.daangn.errand.rest.controller

import com.daangn.errand.domain.errand.ErrandPreview
import com.daangn.errand.rest.dto.errand.GetErrandResDto
import com.daangn.errand.rest.dto.main.GetCurrentDataResDto
import com.daangn.errand.rest.resolver.TokenPayload
import com.daangn.errand.service.ErrandService
import com.daangn.errand.service.UserService
import com.daangn.errand.support.response.ErrandResponse
import com.daangn.errand.util.JwtPayload
import io.swagger.annotations.Api
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/errands")
@Api(tags = ["메인 심부름 리스트 관련 API"])
class MainController(
    private val errandService: ErrandService,
    private val userService: UserService
) {
    @GetMapping("/current-data") // 토큰 필요없음
    fun getCurrentServiceData(): ErrandResponse<GetCurrentDataResDto> {
        val userCnt = userService.getTotalUserCnt()
        val matchedErrandRate = errandService.getMatchedErrandRate()
        val alarmOnUsersCnt = userService.getAlarmOnUSerCnt()
        return ErrandResponse(GetCurrentDataResDto(userCnt, matchedErrandRate, alarmOnUsersCnt))
    }

    @GetMapping("")
    fun getMain(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "마지막 심부름 ID", required = false) @RequestParam(
            value = "lastId",
            required = false
        ) lastId: Long?,
        @ApiParam(value = "가져오려는 심부름 개수") @RequestParam(value = "size") size: Long,
        @ApiParam(value = "사용자의 지역 ID") @RequestParam(value = "regionId") regionId: String
    ): ErrandResponse<List<GetErrandResDto<ErrandPreview>>> {
        return ErrandResponse(errandService.readMain(payload.userId, lastId, size, regionId))
    }

    @GetMapping("/appliable")
    fun getMainOnlyAppliable(
        @ApiIgnore @TokenPayload payload: JwtPayload,
        @ApiParam(value = "마지막 심부름 ID", required = false) @RequestParam(
            value = "lastId",
            required = false
        ) lastId: Long?,
        @ApiParam(value = "가져오려는 심부름 개수") @RequestParam(value = "size") size: Long,
        @ApiParam(value = "사용자의 지역 ID") @RequestParam(value = "regionId") regionId: String
    ): ErrandResponse<List<GetErrandResDto<ErrandPreview>>> {
        return ErrandResponse(errandService.readMainOnlyAppliable(payload.userId, lastId, size, regionId))
    }
}