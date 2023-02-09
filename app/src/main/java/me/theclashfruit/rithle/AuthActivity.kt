package me.theclashfruit.rithle

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.classes.ListDiffCallback
import me.theclashfruit.rithle.classes.MrApiUrlUtil
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.GitHubAccessTokenModel
import me.theclashfruit.rithle.models.ModrinthSearchModel

class AuthActivity : AppCompatActivity() {
    private var progressTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val action: String? = intent?.action
        val data: Uri?      = intent?.data

        progressTextView = findViewById(R.id.progressTextView)

        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        val format = Json { ignoreUnknownKeys = true }

        // https://api.modrinth.com/v2/user

        val jsonObjectRequest = object : StringRequest(
            Method.POST, "https://github.com/login/oauth/access_token",
            { response ->
                val responseJson = format.decodeFromString<GitHubAccessTokenModel>(response)

                sharedPref.edit()
                    .putString("authToken", responseJson.access_token!!)
                    .apply()

                getMrUser(responseJson.access_token!!)

                progressTextView!!.text = "Requesting user from Modrinth..."

                Log.d("AuthUri", responseJson.access_token!!)
            },
            { error ->
                progressTextView!!.text = resources.getString(R.string.auth_error_ghstep)

                error.networkResponse.allHeaders?.forEach {
                    Log.e("webCall", it.name + ": " + it.value)
                }

                Log.e("webCall", "Code: " + error.networkResponse.statusCode.toString())
                Log.e("webCall", "Data: " + error.networkResponse.data.decodeToString())
                Log.e("webCall", "Time: " + error.networkResponse.networkTimeMs.toString())
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["client_id"] = BuildConfig.GH_CLIENT
                params["client_secret"] = BuildConfig.GH_SECRET
                params["code"] = data!!.getQueryParameter("code")!!.trim()
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8"
                return headers
            }
        }

        RithleSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun getMrUser(accessToken: String) {
        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, MrApiUrlUtil().getApiUrl() + "/v2/user", null,
            { response ->
                progressTextView!!.text = "Welcome ${response.getString("username")}!"

                sharedPref.edit()
                    .putString("userName", response.getString("username"))
                    .apply()

                finish()
            },
            { error ->
                progressTextView!!.text = resources.getString(R.string.auth_error_mrstep, accessToken)
                Log.e("webCall", error.stackTraceToString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                headers["Authorization"] = accessToken
                return headers
            }
        }

        RithleSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}