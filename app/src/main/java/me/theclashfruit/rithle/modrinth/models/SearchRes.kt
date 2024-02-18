package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class SearchRes(
    val hits: List<SearchResult>,
    val offset: Int,
    val limit: Int,
    @SerializedName("total_hits") val totalHits: Int
)
