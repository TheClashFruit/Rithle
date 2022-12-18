package me.theclashfruit.mrapi.serializers

import kotlinx.serialization.Serializable

@Serializable
data class ModrinthProjectModel (
  var id                    : String?                              = null,
  var slug                  : String?                              = null,
  var project_type          : String?                              = null,
  var team                  : String?                              = null,
  var title                 : String?                              = null,
  var description           : String?                              = null,
  var body                  : String?                              = null,
  var body_url              : String?                              = null,
  var published             : String?                              = null,
  var updated               : String?                              = null,
  var approved              : String?                              = null,
  var status                : String?                              = null,
  var moderator_message     : ModrinthModMessageModel?             = ModrinthModMessageModel(),
  var license               : ModrinthLicenseModel?                = ModrinthLicenseModel(),
  var client_side           : String?                              = null,
  var server_side           : String?                              = null,
  var downloads             : Int?                                 = null,
  var followers             : Int?                                 = null,
  var categories            : ArrayList<String>                    = arrayListOf(),
  var additional_categories : ArrayList<String>                    = arrayListOf(),
  var versions              : ArrayList<String>                    = arrayListOf(),
  var icon_url              : String?                              = null,
  var issues_url            : String?                              = null,
  var source_url            : String?                              = null,
  var wiki_url              : String?                              = null,
  var discord_url           : String?                              = null,
  var donation_urls         : ArrayList<ModrinthDonationUrlsModel> = arrayListOf(),
  var gallery               : ArrayList<ModrinthGalleryModel>      = arrayListOf()
)