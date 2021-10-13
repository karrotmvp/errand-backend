package com.daangn.errand.service

import com.daangn.errand.support.dto.GetRegionInfoRes
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
internal class DaangnServiceTest constructor(
    @Autowired val daangnService: DaangnService
){
    @Test
    fun `regionId로 region 정보 가져오기`() {
        val reqRegionId = "6530459d189b"
        val resData: GetRegionInfoRes.Data = daangnService.getRegionInfoByRegionId(reqRegionId)
        val regionInfo = resData.region
        Assertions.assertThat(regionInfo.id).isEqualTo(reqRegionId)
        Assertions.assertThat(regionInfo.nodeId).isEqualTo("UmVnaW9uOjY1MzA0NTlkMTg5Yg==")
        Assertions.assertThat(regionInfo.name).isEqualTo("역삼1동")
        Assertions.assertThat(regionInfo.name1).isEqualTo("서울특별시")
        Assertions.assertThat(regionInfo.name2).isEqualTo("강남구")
        Assertions.assertThat(regionInfo.name3).isEqualTo("역삼1동")
    }
}