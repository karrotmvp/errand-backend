package com.daangn.errand.rest.dto.daangn

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
    val profileImageUrl: String? // TODO: 프로필 이미지 필드 추가 되면 이름 확인하고 수정하기
)