package me.theclashfruit.rithle.modrinth

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.toLowerCase
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
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.enums.Index
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.modrinth.serializables.Category
import me.theclashfruit.rithle.modrinth.serializables.GameVersion
import me.theclashfruit.rithle.modrinth.serializables.Loader
import me.theclashfruit.rithle.modrinth.serializables.Project
import me.theclashfruit.rithle.modrinth.serializables.ProjectResult
import me.theclashfruit.rithle.modrinth.serializables.Search
import me.theclashfruit.rithle.modrinth.serializables.TokenResponse
import me.theclashfruit.rithle.modrinth.serializables.User
import java.util.Locale
import java.util.Locale.getDefault

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
            sanitizeHeader { header -> header.lowercase(getDefault()) == "set-cookie" }
            sanitizeHeader { header -> header.lowercase(getDefault()) == "cf-ray" }
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

    // Meta (Tag) Stuff
    private var _gameVersions: List<GameVersion>? = null
    suspend fun gameVersions(): List<GameVersion> {
        _gameVersions?.let { return it }

        val response: HttpResponse = httpClient.get("${url}/v2/tag/game_version")

        val body = response.body<List<GameVersion>>()
        _gameVersions = body

        return body
    }

    private var _loaders: List<Loader>? = null
    suspend fun loaders(): List<Loader> {
        _loaders?.let { return it }

        val response: HttpResponse = httpClient.get("${url}/v3/tag/loader")

        val body = response.body<List<Loader>>()
        _loaders = body

        return body
    }

    private var _categories: List<Category>? = null
    suspend fun categories(): List<Category> {
        _categories?.let { return it }

        val response: HttpResponse = httpClient.get("${url}/v2/tag/category")

        val body = response.body<List<Category>>()
        _categories = body

        return body
    }

    // API
    suspend fun search(
        query: String? = null,
        facets: List<List<String>>? = null,
        index: Index? = null,
        offset: Int = 0,
        limit: Int = 10
    ): Search<ProjectResult> {
        val response: HttpResponse = httpClient.get("${url}/v2/search") {
            url {
                parameters.append("offset", offset.toString())
                parameters.append("limit", limit.toString())

                if (facets != null) {
                    parameters.append("facets", facets.joinToString(
                        prefix = "[",
                        separator = ",",
                        postfix = "]"
                    ) {
                        it.joinToString(
                            prefix = "[\"",
                            separator = "\",\"",
                            postfix = "\"]"
                        )
                    })
                }

                if (query != null) {
                    parameters.append("query", query)
                }

                if (index != null) {
                    parameters.append("index", index.toString())
                }
            }
        }

        return response.body<Search<ProjectResult>>()
    }

    suspend fun user(): User {
        val response: HttpResponse = httpClient.get("${url}/v2/user")

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