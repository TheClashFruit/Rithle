package me.theclashfruit.rithle.modrinth

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.modrinth.serializables.TokenResponse
import me.theclashfruit.rithle.modrinth.serializables.User

class Modrinth(private val staging: Boolean = false) {
    private val url = if (staging) "https://staging-api.modrinth.com" else "https://api.modrinth.com"

    var userToken: String? = null

    private val httpClient: HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        install(UserAgent) {
            agent = "Rithle/${BuildConfig.VERSION_NAME} (https://github.com/TheClashFruit/Rithle)"
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE

            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }

        defaultRequest {
            userToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    fun close() {
        httpClient.close()
    }

    // Singleton stuff...
    companion object {
        @Volatile
        private var instance: Modrinth? = null

        fun getInstance(staging: Boolean = false): Modrinth =
            instance ?: synchronized(this) {
                instance ?: Modrinth(staging).also { instance = it }
            }
    }

    val authenticated: Boolean
        get() = userToken != null

    suspend fun user(): User {
        val response: HttpResponse = httpClient.get("${url}/v2/user")

        Log.d("ModrinthApiRaw", response.bodyAsText())

        return response.body<User>()
    }

    // OAuth stuff
    inner class OAuth(
        private val client: String,
        private val secret: String,
    ) {
        fun authorizationUrl(
            redirect: String,
            scopes: List<Scope>,
            state: String? = null
        ): String {
            val uri = URLBuilder("https://modrinth.com/auth/authorize")

            uri.parameters.append("response_type", "code")
            uri.parameters.append("client_id", this.client)
            uri.parameters.append("redirect_uri", redirect)
            uri.parameters.append("scope", scopes.joinToString(" ") { it.value })

            if (state != null) {
                uri.parameters.append("state", state)
            }

            return uri.toString()
        }

        suspend fun token(
            code: String,
            redirect: String
        ): TokenResponse? {
            val response: HttpResponse = httpClient.post("${url}/_internal/oauth/token") {
                contentType(ContentType.Application.FormUrlEncoded)

                header(HttpHeaders.Authorization, secret)

                setBody(
                    FormDataContent(Parameters.build {
                        append("code", code)
                        append("redirect_uri", redirect)
                        append("client_id", client)
                        append("grant_type", "authorization_code")
                    })
                )
            }

            if (response.status == HttpStatusCode.Unauthorized) return null

            return response.body<TokenResponse>()
        }
    }
}