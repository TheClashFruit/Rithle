package me.theclashfruit.rithle.modrinth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.enums.Scope
import me.theclashfruit.rithle.modrinth.serializables.TokenResponse

class Modrinth(private val staging: Boolean = false) {
    private val url = if (staging) "https://staging-api.modrinth.com" else "https://api.modrinth.com"

    private val httpClient: HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }

        install(UserAgent) {
            agent = "Rithle/${BuildConfig.VERSION_NAME} (https://github.com/TheClashFruit/Rithle)"
        }
    }

    var userToken: String? = null

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