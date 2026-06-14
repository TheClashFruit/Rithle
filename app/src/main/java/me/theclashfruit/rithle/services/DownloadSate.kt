package me.theclashfruit.rithle.services

data class DownloadSate(
    val isDownloading: Boolean = false,
    val isCompleted: Boolean = false,

    val bytesDownloaded: Long = 0L,
    val totalBytes: Long = 0L,

    val percentage: Float = 0f,
    val fileName: String? = null,

    val error: String? = null
)
