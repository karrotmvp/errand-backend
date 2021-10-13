package com.daangn.errand.util

import com.daangn.errand.rest.dto.daangn.GetRegionInfoRes
import com.daangn.errand.util.DaangnUtil
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
internal class DaangnUtilTest constructor(
    @Autowired val daangnUtil: DaangnUtil
){
    @Test
    fun `regionId로 region 정보 가져오기`() {
        val reqRegionId = "6530459d189b"
        val resData: GetRegionInfoRes.Data = daangnUtil.getRegionInfoByRegionId(reqRegionId)
        val regionInfo = resData.region
        Assertions.assertThat(regionInfo.id).isEqualTo(reqRegionId)
        Assertions.assertThat(regionInfo.nodeId).isEqualTo("UmVnaW9uOjY1MzA0NTlkMTg5Yg==")
        Assertions.assertThat(regionInfo.name).isEqualTo("역삼1동")
        Assertions.assertThat(regionInfo.name1).isEqualTo("서울특별시")
        Assertions.assertThat(regionInfo.name2).isEqualTo("강남구")
        Assertions.assertThat(regionInfo.name3).isEqualTo("역삼1동")
    }
}