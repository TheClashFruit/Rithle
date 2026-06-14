package me.theclashfruit.rithle.services

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.serializables.File
import me.theclashfruit.rithle.modrinth.serializables.Version
import me.theclashfruit.rithle.services.serializables.DownloadMeta
import me.theclashfruit.rithle.services.serializables.DownloadReason
import java.util.Locale.getDefault
import androidx.core.net.toUri
import io.ktor.utils.io.readAvailable

class DownloadService(private val context: Context) {
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
        folderPath: String,
        fileName: String
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
                val channel = response.bodyAsChannel()

                val outputStream = if (folderPath.startsWith("content://")) {
                    val directoryUri = folderPath.toUri()
                    val directory = DocumentFile.fromTreeUri(context, directoryUri)
                    val file = directory!!.findFile(fileName) ?: directory.createFile("application/octet-stream", fileName)

                    context.contentResolver.openOutputStream(file!!.uri)
                } else {
                    val folder = java.io.File(folderPath)
                    if (!folder.exists()) folder.mkdirs()

                    val outputFile = java.io.File(folder, fileName)
                    outputFile.outputStream()
                }

                outputStream.use { fileOutputStream ->
                    val buffer = ByteArray(8192)
                    while (!channel.isClosedForRead) {
                        val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                        if (bytesRead == -1) break // EOF reached
                        if (bytesRead > 0) {
                            fileOutputStream!!.write(buffer, 0, bytesRead)
                        }
                    }
                }

                outputStream!!.close()

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
                    folderPath = path,
                    fileName = primaryFile.filename
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
                    folderPath = path,
                    fileName = file.filename
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