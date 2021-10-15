package com.daangn.errand.rest.dto.daangn

data class GetNeighborRegionInfoRes(
    val data: Data
) {
    class Data(
        val region: RegionWithNeighbor
    )
}

class RegionWithNeighbor(
    val id: String,
    val nodeId: String,
    val neighborRegions: List<Region>
)