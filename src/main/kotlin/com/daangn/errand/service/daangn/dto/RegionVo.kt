package com.daangn.errand.service.daangn.dto

data class RegionVo (
    val id: String,
    val nodeId: String,
    val name: String,
    val name1: String,
    val name2: String,
    val name3: String
) {
    companion object {
        fun from(region: Region): RegionVo {
            return RegionVo(
                id = region.id,
                nodeId = region.nodeId,
                name = region.name,
                name1 = region.name1,
                name2 = region.name2,
                name3 = region.name3
            )
        }
    }
}
