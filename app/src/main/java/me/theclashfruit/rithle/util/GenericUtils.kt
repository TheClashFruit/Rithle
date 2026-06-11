package me.theclashfruit.rithle.util

import android.content.Context
import android.icu.text.CompactDecimalFormat
import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
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