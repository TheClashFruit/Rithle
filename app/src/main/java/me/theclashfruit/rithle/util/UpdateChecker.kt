package me.theclashfruit.rithle.util

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.util.serializables.GitHubRelease
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class UpdateChecker {
    companion object {
        private val httpClient: HttpClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }

        @OptIn(ExperimentalTime::class)
        suspend fun checkUpdate(): UpdateData {
            try {
                val response: HttpResponse = httpClient.get("https://api.github.com/repos/TheClashFruit/Rithle/releases")
                val data = response.body<List<GitHubRelease>>()

                val latestData = data.maxByOrNull { Instant.parse(it.publishedAt) }!!

                val current = SemVer(BuildConfig.VERSION_NAME)
                val latest = SemVer(latestData.tagName)

                Log.d("UpdateChecker", "Latest: $latest")
                Log.d("UpdateChecker", "Current: $current")

                Log.d("UpdateChecker", (current < latest).toString())

                return if (current < latest)
                    UpdateData(
                        true,
                        latestData
                    )
                else UpdateData(false)
            } catch (_: Exception) {
                // failed to check updates
                return UpdateData(false)
            }
        }
    }

    data class UpdateData(
        val hasUpdate: Boolean,

        val data: GitHubRelease? = null
    )
}