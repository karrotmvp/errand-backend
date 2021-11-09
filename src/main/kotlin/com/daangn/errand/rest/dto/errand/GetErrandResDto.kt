package com.daangn.errand.rest.dto.errand

import com.daangn.errand.domain.errand.ErrandDto
import com.daangn.errand.domain.errand.ErrandHasStatus
import com.daangn.errand.domain.errand.ErrandPreview
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetErrandResDto<T: ErrandHasStatus>(
    var errand: T,
    var isMine: Boolean,
    var didIApply: Boolean,
    var wasIChosen: Boolean,
    var helpId: Long? = null
)


