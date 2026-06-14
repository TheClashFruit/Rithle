package me.theclashfruit.rithle.services

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.jvm.javaio.copyTo
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.File
import me.theclashfruit.rithle.modrinth.serializables.Version
import me.theclashfruit.rithle.services.serializables.DownloadMeta
import me.theclashfruit.rithle.services.serializables.DownloadReason
import java.util.Locale.getDefault

class DownloadService {
    private val modrinth = Modrinth.getInstance()

    private val httpClient: HttpClient = HttpClient(Android) {
        install(UserAgent) {
            agent = "Rithle/${BuildConfig.VERSION_NAME} (https://github.com/TheClashFruit/Rithle)"
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = if (BuildConfig.DEBUG) LogLevel.HEADERS else LogLevel.NONE

            sanitizeHeader { header -> header == HttpHeaders.Authorization }
            sanitizeHeader { header -> header.lowercase(getDefault()) == "set-cookie" }
            sanitizeHeader { header -> header.lowercase(getDefault()) == "cf-ray" }
        }

        install(HttpCache)
    }

    private fun download(
        url: String,
        meta: DownloadMeta,
        outputFile: java.io.File
    ): Flow<DownloadSate> = channelFlow {
        send(DownloadSate(
            isDownloading = true
        ))

        try {
            httpClient.prepareGet(url) {
                header("modrinth-download-meta", Json.encodeToString(meta))
                
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null) {
                        if (contentLength > 0) {
                            val percent = bytesSentTotal.toFloat() / contentLength.toFloat()

                            trySend(DownloadSate(
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
                outputFile.parentFile?.mkdirs()

                Log.d("Download", outputFile.absolutePath)

                val channel = response.bodyAsChannel()
                val stream = outputFile.outputStream()
                stream.use { fileOutputStream ->
                    channel.copyTo(fileOutputStream)
                }
                stream.close()

                send(
                    DownloadSate(
                        isDownloading = false,
                        isCompleted = true,

                        percentage = 1f
                    )
                )
            }

            
        } catch (e: Exception) {
            Log.e("DownloadError", "3:", e)

            send(DownloadSate(
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
                    url = primaryFile.url,
                    meta = DownloadMeta(
                        reason = DownloadReason.Standalone,
                        gameVersion = version.gameVersions[0],
                        loader = version.loaders[0],
                    ),
                    outputFile = java.io.File("$path/${primaryFile.filename}")
                )
            )
        } else {
            val filesToDownload = mutableListOf<Pair<File, DownloadMeta>>()

            filesToDownload.add(
                primaryFile to DownloadMeta(
                    reason = DownloadReason.Standalone,
                    gameVersion = version.gameVersions[0],
                    loader = version.loaders[0]
                )
            )

            val dependencyIds = version.dependencies.mapNotNull {
                it.versionId?.takeIf { id -> id.isNotEmpty() }
            }

            if (dependencyIds.isNotEmpty()) {
                try {
                    val versions = modrinth.versions(dependencyIds)
                    versions.forEach { ver ->
                        val depFile = ver.files.find { it.primary } ?: ver.files.firstOrNull()
                        if (depFile != null) {
                            filesToDownload.add(
                                depFile to DownloadMeta(
                                    reason = DownloadReason.Dependency,
                                    gameVersion = ver.gameVersions[0],
                                    loader = ver.loaders[0]
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    emit(DownloadSate(error = "Failed to resolve dependencies: ${e.localizedMessage}"))
                    return@flow
                }
            }

            val totalFilesCount = filesToDownload.size

            filesToDownload.forEachIndexed { index, (file, meta) ->
                val fileProgressFlow = download(
                    url = file.url,
                    meta = meta,
                    outputFile = java.io.File("$path/${file.filename}")
                )

                fileProgressFlow.collect { subState ->
                    if (subState.error != null) {
                        emit(DownloadSate(error = subState.error))
                        return@collect
                    }

                    val globalPercentage = (index + subState.percentage) / totalFilesCount

                    emit(
                        DownloadSate(
                            isDownloading = true,
                            bytesDownloaded = subState.bytesDownloaded,
                            totalBytes = subState.totalBytes,
                            percentage = globalPercentage
                        )
                    )
                }
            }

            emit(
                DownloadSate(
                    isDownloading = false,
                    isCompleted = true,
                    percentage = 1f
                )
            )
        }
    }
}