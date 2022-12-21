package me.theclashfruit.rithle.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModrinthSearchHitsModel(
  @SerializedName("project_id"         ) var project_id         : String?           = null,
  @SerializedName("project_type"       ) var project_type       : String?           = null,
  @SerializedName("slug"               ) var slug               : String?           = null,
  @SerializedName("author"             ) var author             : String?           = null,
  @SerializedName("title"              ) var title              : String?           = null,
  @SerializedName("description"        ) var description        : String?           = null,
  @SerializedName("categories"         ) var categories         : ArrayList<String> = arrayListOf(),
  @SerializedName("display_categories" ) var display_categories : ArrayList<String> = arrayListOf(),
  @SerializedName("versions"           ) var versions           : ArrayList<String> = arrayListOf(),
  @SerializedName("downloads"          ) var downloads          : Int?              = null,
  @SerializedName("follows"            ) var follows            : Int?              = null,
  @SerializedName("icon_url"           ) var icon_url           : String?           = null,
  @SerializedName("date_created"       ) var date_created       : String?           = null,
  @SerializedName("date_modified"      ) var date_modified      : String?           = null,
  @SerializedName("latest_version"     ) var latest_version     : String?           = null,
  @SerializedName("license"            ) var license            : String?           = null,
  @SerializedName("client_side"        ) var client_side        : String?           = null,
  @SerializedName("server_side"        ) var server_side        : String?           = null,
  @SerializedName("gallery"            ) var gallery            : ArrayList<String> = arrayListOf()
)