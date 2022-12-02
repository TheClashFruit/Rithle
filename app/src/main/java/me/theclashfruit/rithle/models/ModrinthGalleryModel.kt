package me.theclashfruit.rithle.models

import kotlinx.serialization.Serializable

@Serializable
data class ModrinthGalleryModel (
  var url         : String?  = null,
  var featured    : Boolean? = null,
  var title       : String?  = null,
  var description : String?  = null,
  var created     : String?  = null
)