package me.theclashfruit.mrapi

import android.content.Context

class ApiWrapper constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: ApiWrapper? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiWrapper(context).also {
                    INSTANCE = it
                }
            }
    }

    var projectName  = "Rithle"
    var projectVer   = "0.1.0"
    var projectUrl   = "github.com/TheClashFruit/Rithle"
    var projectEmail = "admin@theclashfruit.me"

    fun searchProjects(filter: String) {

    }
}