package com.daangn.errand.service.daangn.dto

data class GetNeighborRegionInfoRes(
    val data: Data
) {
    class Data(
        val region: RegionWithNeighbor
    )

    fun getRegionIds(): List<String> {
        return data.region.neighborRegions.map { it.id }
    }
}

class RegionWithNeighbor(
    val id: String,
    val nodeId: String,
    val neighborRegions: List<Region>
)
