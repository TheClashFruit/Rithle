package me.theclashfruit.rithle

import android.app.Application
import com.google.android.material.color.DynamicColors

class RithleApplication : Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)

        super.onCreate()
    }
}