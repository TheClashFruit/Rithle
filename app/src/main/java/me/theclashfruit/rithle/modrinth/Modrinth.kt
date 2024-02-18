package me.theclashfruit.rithle.modrinth

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.modrinth.models.SearchRes
import me.theclashfruit.rithle.modrinth.models.SearchResult
import me.theclashfruit.rithle.modrinth.models.User
import me.theclashfruit.rithle.util.FileSize
import me.theclashfruit.rithle.util.FileSize.SizeUnit
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Error


class Modrinth {
    private val logTag = "ModrinthWrapper"
    private val apiUri = Uri.parse("https://api.modrinth.com/v2" /* if (BuildConfig.BUILD_TYPE == "release") "https://api.modrinth.com/v2" else "https://staging-api.modrinth.com/v2" */)

    private val gson = Gson()

    companion object {
        private lateinit var requestQueue: RequestQueue
        private lateinit var authToken: String

        @Volatile
        private var instance: Modrinth? = null

        fun getInstance(context: Context): Modrinth {
            if(instance == null) {
                this.instance = Modrinth()

                val diskCache = DiskBasedCache(context.cacheDir, FileSize.toBytes(6, SizeUnit.MB))
                val network   = BasicNetwork(HurlStack())

                requestQueue = RequestQueue(diskCache, network).apply {
                    start()
                }
            }

            return instance!!
        }
    }

    fun setToken(token: String) {
        authToken = token
    }

    // --------------------------------------------- //

    fun search(query: String, facets: List<List<String>>, callback: (List<SearchResult>) -> Unit) {
        val url =
            apiUri
                .buildUpon()
                .appendPath("search")
                .appendQueryParameter("query", query)
                .appendQueryParameter("facets", gson.toJson(facets))
                .appendQueryParameter("limit", "20")
                .build()
                .toString()

        objectRequestHelper(Request.Method.GET, url, null,
            { res ->
                val data = gson.fromJson(res.toString(), SearchRes::class.java)

                callback(data.hits)
            }, { error ->
                throw Error(error.message)
            }
        )
    }

    fun loggedInUser(callback: (User) -> Unit) {
        val url =
            apiUri
                .buildUpon()
                .appendPath("user")
                .build()
                .toString()

        objectRequestHelper(Request.Method.GET, url, null,
            { res ->
                val data = gson.fromJson(res.toString(), User::class.java)

                callback(data)
            },
            { error ->
                throw Error(error.message)
            }
        )
    }

    // --------------------------------------------- //

    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(1024)

                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    // --------------------------------------------- //

    private fun objectRequestHelper(method: Int, url: String, jsonRequest: JSONObject?, callback: (Any) -> Unit, errorCallback: (VolleyError) -> Unit) {
        val jsonObjectRequest = object : JsonObjectRequest(method, url, jsonRequest,
            { res -> callback(res) },
            { error -> errorCallback(error) }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                headers["Authorization"] = authToken

                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    private fun arrayRequestHelper(method: Int, url: String, jsonRequest: JSONArray?, callback: (Any) -> Unit) {
        val jsonArrayRequest = object : JsonArrayRequest(method, url, jsonRequest,
            { res -> callback(res) },
            { error -> Log.e(logTag, error.stackTraceToString()) }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                headers["Authorization"] = authToken

                return headers
            }
        }

        requestQueue.add(jsonArrayRequest)
    }
}