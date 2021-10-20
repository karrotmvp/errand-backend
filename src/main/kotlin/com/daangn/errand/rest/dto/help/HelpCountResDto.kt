package com.daangn.errand.rest.dto.help

data class HelpCountResDto(
    val helperCnt: Long
) {
    val canApply: Boolean = helperCnt < 5
}
