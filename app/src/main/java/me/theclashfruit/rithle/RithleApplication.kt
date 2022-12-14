package me.theclashfruit.rithle

import android.app.Application
import android.os.Process
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class RithleApplication : Application()  {
    override fun onCreate() {
        super.onCreate()

        initializeExceptionHandler()

        val logDelete = File(applicationContext.dataDir.toString() + "/files/latest.log")
        if (logDelete.exists()) {
            val newLogFile = File(applicationContext.dataDir.toString() + "/files/" + SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(Date()) + ".log")

            newLogFile.createNewFile()
            newLogFile.writeText(logDelete.readText())

            logDelete.delete()
        }
    }

    private fun initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            Log.e("RithleApplication", ex.stackTraceToString())

            Runtime.getRuntime().exec(arrayOf("logcat", "-f", applicationContext.dataDir.toString() + "/files/latest.log", "*:V"))

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