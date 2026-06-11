package me.theclashfruit.rithle.util

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.launchCustomTabs(url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(this, url.toUri())
}