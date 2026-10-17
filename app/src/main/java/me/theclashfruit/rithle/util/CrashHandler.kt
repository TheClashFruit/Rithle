package me.theclashfruit.rithle.util

import android.content.Context
import android.os.Build
import android.util.Log
import me.theclashfruit.rithle.BuildConfig
import org.json.JSONObject
import java.io.File
import kotlin.io.writeText

class CrashHandler(
    private val context: Context,
    private val previous: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.i("CrashHandler", "`uncaughtException` called.")
        Log.i("CrashHandler", context.filesDir.toString())

        try {
            val json = JSONObject().apply {
                put("app", JSONObject().apply {
                    put("version", "${BuildConfig.VERSION_NAME}|${BuildConfig.VERSION_CODE}")
                    put("commit", BuildConfig.GIT_HASH)
                    put("debug", BuildConfig.DEBUG)
                })

                put("device", JSONObject().apply {
                    put("manufacturer", Build.MANUFACTURER)
                    put("model", Build.MODEL)
                    put("sdk", Build.VERSION.SDK_INT)
                })

                put("timestamp", System.currentTimeMillis())
                put("stacktrace", e.stackTraceToString())
            }

            File(context.filesDir, "crash_${System.currentTimeMillis()}.json").writeText(json.toString())
        } catch (err: Exception) {
            Log.e("CrashHandler", "Failed to save crash file.", err)
        }

        previous?.uncaughtException(t, e)
    }
}