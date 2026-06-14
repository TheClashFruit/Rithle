package me.theclashfruit.rithle.modrinth.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Side(private val value: String) {
    @SerialName("required")    Required("required"),
    @SerialName("optional")    Optional("optional"),
    @SerialName("unsupported") Unsupported("unsupported"),
    @SerialName("unknown")     Unknown("unknown");

    override fun toString(): String {
        return value
    }

    fun getName(): String {
        return value
    }
}