package com.daangn.errand.service.daangn.dto

data class GetUserInfoByUserIdRes(
    val data: Data
) {
    data class Data(
        val user: DaangnUserInfo
    )
}

data class GetUserInfoByUserIdListRes (
    val data: Data
        ) {
    data class Data (
        val users: List<DaangnUserInfo>
            )
}

data class DaangnUserInfo(
    val id: String,
    val nickname: String?,
    val profileImageUrl: String?,
    val mannerTemperature: Float?,
)
