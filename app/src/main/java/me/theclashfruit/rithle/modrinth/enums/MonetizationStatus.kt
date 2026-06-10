package me.theclashfruit.rithle.modrinth.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MonetizationStatus(private val value: String) {
    @SerialName("monetized")         Monetized("monetized"),
    @SerialName("demonetized")       Demonetized("demonetized"),
    @SerialName("force-demonetized") ForceDemonetized("force-demonetized");

    override fun toString(): String {
        return value
    }
}