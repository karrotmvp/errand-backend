package com.daangn.errand.domain.image

import com.daangn.errand.domain.errand.ErrandVo

data class ImageVo(
    val id: Long?,
    val url: String,
    val errand: ErrandVo
)
