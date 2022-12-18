package me.theclashfruit.mrapi.serializers

import kotlinx.serialization.Serializable

@Serializable
data class ModrinthLicenseModel (
  var id   : String? = null,
  var name : String? = null,
  var url  : String? = null
)