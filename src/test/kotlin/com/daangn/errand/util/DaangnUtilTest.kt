package com.daangn.errand.util

import com.daangn.errand.rest.dto.daangn.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class DaangnUtilTest constructor(
    @Autowired val daangnUtil: DaangnUtil,
    @Autowired val regionConverter: RegionConverter
) {
    @Test
    fun `regionId로 region 정보 가져오기`() {
        val reqRegionId = "6530459d189b"
        val region =regionConverter.toRegionVo(daangnUtil.getRegionInfoByRegionId(reqRegionId).region)
        Assertions.assertThat(region.id).isEqualTo(reqRegionId)
        Assertions.assertThat(region.nodeId).isEqualTo("UmVnaW9uOjY1MzA0NTlkMTg5Yg==")
        Assertions.assertThat(region.name).isEqualTo("역삼1동")
        Assertions.assertThat(region.name1).isEqualTo("서울특별시")
        Assertions.assertThat(region.name2).isEqualTo("강남구")
        Assertions.assertThat(region.name3).isEqualTo("역삼1동")
    }

    @Test
    fun `비즈 채팅 보내기`() {
        val actions: List<Action> = listOf(
            Action(
                type = "PRIMARY_BUTTON",
                payload = Payload("https://www.daangn.com", "누르면 이동해요")
            )
        )
        val postBizChatReq = PostBizChatReq(
            Input(
                userId = "8a190fa9bb5d4d89b3944dc8c5b3a102",
                title = "심부름 서버 비즈 채팅 api 테스트입니다",
                text = "잘 가고 있나요?",
                actions = actions
            )
        )
        assertDoesNotThrow {
            daangnUtil.sendBizChatting(postBizChatReq)
        }

    }

    @Test
    fun `주변 지역 정보 가져오기`() {
        val regionId = "6530459d189b"
        assertDoesNotThrow { daangnUtil.getNeighborRegionByRegionId(regionId) }
    }
}