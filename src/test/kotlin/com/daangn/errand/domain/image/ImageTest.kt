package com.daangn.errand.domain.image

import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class ImageTest constructor(
    @Autowired val imageConverter: ImageConverter
) {
    @Test
    fun `convert Image to ImageVo`() {
        val image = Image(
            "http://image.url",
            Errand(
                customer = User("mockDaangnId"),
                category = Category(),
                regionId = "6530459d189b",
                detailAddress = "1F",
                gratuity = "10000",
                detail = "심부름 상세 요청 정보"
            )
        )
        val imageVo = imageConverter.toImageVo(image)
        Assertions.assertThat(imageVo.url).isEqualTo(image.url)
    }
}