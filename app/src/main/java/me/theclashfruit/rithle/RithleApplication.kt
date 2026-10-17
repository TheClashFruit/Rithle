package me.theclashfruit.rithle

import android.app.Application
import me.theclashfruit.rithle.util.CrashHandler

class RithleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler(
            CrashHandler(this, Thread.getDefaultUncaughtExceptionHandler())
        )
    }
}