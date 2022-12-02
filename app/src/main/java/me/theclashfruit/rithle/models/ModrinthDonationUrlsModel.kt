package me.theclashfruit.rithle.models

import kotlinx.serialization.Serializable

@Serializable
data class ModrinthDonationUrlsModel (
  var id       : String? = null,
  var platform : String? = null,
  var url      : String? = null
)