package com.daangn.errand.rest.dto.daangn

import datadog.trace.api.Trace

data class GetRegionInfoRes(
    val data: Data
) {
    data class Data(
        val region: Region
    )
}

data class GetRegionInfoListRes(
    val data: Data
) {
    data class Data(
        val regions: MutableList<Region>
    )
}

data class Region(
    val id: String,
    val nodeId: String,
    val name: String,
    val name1: String,
    val name1Id: String,
    val name2: String,
    val name2Id: String,
    val name3: String,
    val name3Id: String
) {
    companion object {
        fun convertRegionListToRegionIdList(regions: List<Region>) = regions.asSequence().map { region ->
            region.id
        }.toList()
    }
}
