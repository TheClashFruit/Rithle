package me.theclashfruit.mrapi.serializers

import kotlinx.serialization.Serializable

@Serializable
data class ModrinthDonationUrlsModel (
  var id       : String? = null,
  var platform : String? = null,
  var url      : String? = null
)