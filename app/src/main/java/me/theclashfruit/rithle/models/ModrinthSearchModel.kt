package me.theclashfruit.rithle.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ModrinthSearchModel(
  @SerializedName("hits"       ) var hits      : ArrayList<ModrinthSearchHitsModel> = arrayListOf(),
  @SerializedName("offset"     ) var offset    : Int?            = null,
  @SerializedName("limit"      ) var limit     : Int?            = null,
  @SerializedName("total_hits" ) var totalHits : Int?            = null
)