package me.theclashfruit.rithle.classes

import me.theclashfruit.rithle.BuildConfig

class MrApiUrlUtil {
    private val stableUrl  = "https://api.modrinth.com"
    private val stagingUrl = "https://staging-api.modrinth.com"

    fun getApiUrl(): String {
        return if (BuildConfig.DEBUG) {
            stagingUrl
        } else {
            stableUrl
        }
    }

    fun getIsDebugMode(): Boolean {
        return BuildConfig.DEBUG
    }
}