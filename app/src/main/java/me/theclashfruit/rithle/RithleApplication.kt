package me.theclashfruit.rithle

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process.killProcess
import android.os.Process.myPid
import android.util.Log
import kotlin.system.exitProcess

class RithleApplication : Application()  {
    override fun onCreate() {
        super.onCreate()

        initializeExceptionHandler()
    }

    private fun initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            Log.e("RithleApplication", "CrashException", ex)

            val intent = Intent(applicationContext, DebugActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("error", ex.stackTraceToString())
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am[AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000] = pendingIntent

            killProcess(myPid())
        }
    }
}