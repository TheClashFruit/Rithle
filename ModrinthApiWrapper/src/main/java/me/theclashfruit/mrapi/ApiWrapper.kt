package me.theclashfruit.mrapi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.mrapi.serializers.ModrinthProjectModel

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

    fun searchProjects(filter: String, context: Context) {

    }

    fun getProject(project: String, context: Context): ModrinthProjectModel {
        val format = Json { ignoreUnknownKeys = true }
        var data: String? = null

        val jsonObjectRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/project/${project}", null,
            { response ->
                data = response.toString()
            },
            { error ->
                Log.e("webCall", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/0.0.1 (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Fuel/2.3.1"
                return headers
            }
        }

        VolleySingleton.getInstance(context.applicationContext).addToRequestQueue(jsonObjectRequest)
        return format.decodeFromString(data!!)
    }

    fun getProjectRaw(project: String, context: Context): String {
        val format = Json { ignoreUnknownKeys = true }
        var data: String? = null

        val jsonObjectRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/project/${project}", null,
            { response ->
                data = response.toString()
            },
            { error ->
                Log.e("webCall", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/0.0.1 (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Fuel/2.3.1"
                return headers
            }
        }

        VolleySingleton.getInstance(context.applicationContext).addToRequestQueue(jsonObjectRequest)
        return data!!
    }
}