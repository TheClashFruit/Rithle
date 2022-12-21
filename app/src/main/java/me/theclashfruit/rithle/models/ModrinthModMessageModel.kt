package me.theclashfruit.rithle.models

import kotlinx.serialization.Serializable

@Serializable
data class ModrinthModMessageModel(
    var message : String? = null,
    var body    : String? = null
)