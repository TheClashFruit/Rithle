package me.theclashfruit.rithle.services

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.File
import me.theclashfruit.rithle.modrinth.serializables.Version
import me.theclashfruit.rithle.services.serializables.DownloadMeta
import me.theclashfruit.rithle.services.serializables.DownloadReason

class DownloadService {
    private val modrinth = Modrinth.getInstance()

    private val httpClient: HttpClient = HttpClient(Android) {
        install(UserAgent) {
            agent = "Rithle/${BuildConfig.VERSION_NAME} (https://github.com/TheClashFruit/Rithle)"
        }

        install(HttpCache)
    }

    private fun download(
        url: String,
        meta: DownloadMeta,
        outputFile: java.io.File
    ): Flow<DownloadSate> = flow {
        emit(DownloadSate(
            isDownloading = true
        ))

        try {
            httpClient.prepareGet(url) {
                header("modrinth-download-meta", Json.encodeToString(meta))
                
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null) {
                        if (contentLength > 0) {
                            val percent = bytesSentTotal.toFloat() / contentLength.toFloat()

                            emit(DownloadSate(
                                isDownloading = true,

                                bytesDownloaded = bytesSentTotal,
                                totalBytes = contentLength,

                                percentage = percent
                            ))
                        }
                    }
                }
            }.execute { response ->
                // TODO: Save file

                emit(
                    DownloadSate(
                        isDownloading = false,
                        isCompleted = true,

                        percentage = 1f
                    )
                )
            }

            
        } catch (e: Exception) {
            emit(DownloadSate(
                isDownloading = false,
                error = e.localizedMessage
            ))
        }
    }

    fun download(
        version: Version,
        resolveDependencies: Boolean,
        path: String
    ): Flow<DownloadSate> = flow {
        val primaryFile: File? = version.files.find { it.primary } ?: version.files.firstOrNull()

        if (primaryFile == null) {
            emit(DownloadSate(error = "No valid files found for this version."))
            return@flow
        }

        if (!resolveDependencies) {
            emitAll(
                download(
                    url = primaryFile!!.url,
                    meta = DownloadMeta(
                        reason = DownloadReason.Standalone,
                        gameVersion = version.gameVersions[0],
                        loader = version.loaders[0],
                    ),
                    outputFile = java.io.File("$path/${primaryFile!!.filename}")
                )
            )
        } else {
            val filesToDownload = mutableListOf<Pair<File, DownloadMeta>>()

            download(
                url = primaryFile!!.url,
                meta = DownloadMeta(
                    reason = DownloadReason.Standalone,
                    gameVersion = version.gameVersions[0],
                    loader = version.loaders[0],
                ),
                outputFile = java.io.File("$path/${primaryFile!!.filename}")
            )

            val versions = modrinth.versions(
                version
                        .dependencies
                        .mapNotNull {
                            it.versionId?.takeIf { id -> id.isNotEmpty() }
                        }
            )

            versions.forEach { ver ->
                val primaryFile: File? = ver.files.find { it.primary } ?: version.files.firstOrNull()

                download(
                    url = primaryFile!!.url,
                    meta = DownloadMeta(
                        reason = DownloadReason.Dependency,
                        gameVersion = ver.gameVersions[0],
                        loader = ver.loaders[0],
                    ),
                    outputFile = java.io.File("$path/${primaryFile!!.filename}")
                )
            }
        }
    }
}