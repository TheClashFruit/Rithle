package me.theclashfruit.rithle.modrinth.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Status(private val value: String) {
    @SerialName("approved")   Approved("approved"),
    @SerialName("archived")   Archived("archived"),
    @SerialName("rejected")   Rejected("rejected"),
    @SerialName("draft")      Draft("draft"),
    @SerialName("unlisted")   Unlisted("unlisted"),
    @SerialName("processing") Processing("processing"),
    @SerialName("withheld")   Withheld("withheld"),
    @SerialName("scheduled")  Scheduled("scheduled"),
    @SerialName("private")    Private("private"),
    @SerialName("unknown")    Unknown("unknown");

    override fun toString(): String {
        return value
    }
}