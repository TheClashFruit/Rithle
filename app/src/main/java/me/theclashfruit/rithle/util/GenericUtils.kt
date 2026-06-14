package me.theclashfruit.rithle.util

import android.content.Context
import android.icu.text.CompactDecimalFormat
import android.net.Uri
import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.util.Locale

fun Context.launchCustomTabs(url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(this, url.toUri())
}

fun formatCount(n: Int): String =
    CompactDecimalFormat
        .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(n)

fun timeAgo(iso: String): String {
    val millis = OffsetDateTime.parse(iso).toInstant().toEpochMilli()
    return DateUtils.getRelativeTimeSpanString(
        millis,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

fun getFriendlyPath(uriString: String?): String? {
    if (uriString.isNullOrEmpty()) return null
    return try {
        val uri = uriString.toUri()
        val decodedUri = URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8.name())
        val treeMarker = "/tree/"

        if (decodedUri.contains(treeMarker)) {
            val pathSegment = decodedUri.substringAfter(treeMarker)

            val parts = pathSegment.split(":")

            if (parts.size > 1) {
                val root = parts[0]
                val relativePath = parts[1]

                val absoluteRoot = if (root == "primary") "/storage/emulated/0" else "/storage/$root"
                return "$absoluteRoot/$relativePath".replace("//", "/").trimEnd('/')
            }
        }

        uri.lastPathSegment
    } catch (e: Exception) {
        null
    }
}

suspend fun exportLogcatToUri(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
    try {
        val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-v", "threadtime"))

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            process.inputStream.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        process.waitFor()
    } catch (_: Exception) { }
}