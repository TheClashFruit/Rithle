package me.theclashfruit.rithle

import android.app.Application
import android.os.Process
import android.util.Log
import com.google.android.material.color.DynamicColors
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class RithleApplication : Application()  {
    override fun onCreate() {
        //DynamicColors.applyToActivitiesIfAvailable(this)

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

            val newLogFile = File(applicationContext.dataDir.toString() + "/files/latest.log")

            newLogFile.writeText("------ BEGINNING OF CRASH\n" + ex.stackTraceToString() + "\n------------------")

            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }
}