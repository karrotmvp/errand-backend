package com.daangn.errand.rest.dto.daangn

data class GetRegionInfoRes(
    val data: Data
) {
    data class Data(
        val region: Region
    )
}

class Region(
    val id: String,
    val nodeId: String,
    val name: String,
    val name1: String,
    val name1Id: String,
    val name2: String,
    val name2Id: String,
    val name3: String,
    val name3Id: String
)
