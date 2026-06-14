package me.theclashfruit.rithle.modrinth.serializables

import kotlinx.serialization.Serializable

@Serializable
data class DonationUrl(
    val id: String,
    val platform: String,
    val url: String
)
