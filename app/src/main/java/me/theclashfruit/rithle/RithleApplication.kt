package me.theclashfruit.rithle

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.system.exitProcess

class RithleApplication : Application()  {
    override fun onCreate() {
        super.onCreate()

        initializeExceptionHandler()

        Log.d("logPath", applicationContext.dataDir.toString())
    }

    private fun initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            Log.e("RithleApplication", "CrashException", ex)

            appendLog("--- CRASH START ---\n${ex.stackTraceToString()}\n--- CRASH END ---")

            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

    private fun appendLog(text: String?) {
        val logFile = File(applicationContext.dataDir.toString() + "/files/latest.log")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}