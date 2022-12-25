package me.theclashfruit.rithle.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton
import org.json.JSONArray
import org.json.JSONObject


class NotificationService : Service() {
    private val theHandler = Handler();

    override fun onCreate() {
        val checkNotifs: Runnable = object : Runnable {
            override fun run() {
                val sharedPref = applicationContext.getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

                val authToken = sharedPref!!.getString("authToken", "")
                val userName  = sharedPref.getString("userName", "TheClashFruit")

                /*
                val jsonObjectRequest = object : JsonArrayRequest(
                    Method.GET, "https://api.modrinth.com/v2/user/${userName}/notifications", null,
                    { response ->
                        for (i in 0..response.length()) {
                            val cNotif = response.getJSONObject(i)

                            val notification: Notification = NotificationCompat.Builder(applicationContext, 1069.toString())
                                .setContentTitle(cNotif.getString("title"))
                                .setContentText(cNotif.getString("text"))
                                .setSmallIcon(R.drawable.ic_build)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .build()

                            with(NotificationManagerCompat.from(applicationContext)) {
                                notify((Math.random() * 1000).toInt(), notification)
                            }
                        }
                    },
                    { error ->
                        Log.e("webCall", error.stackTraceToString())
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                        headers["Authorization"] = authToken.toString()
                        return headers
                    }
                }

                RithleSingleton.getInstance(applicationContext).addToRequestQueue(jsonObjectRequest)
*/
                theHandler.postDelayed(this, 300000)
            }
        }

        checkNotifs.run()

        val channel = NotificationChannel(1069.toString(), "Rithle Notification Service", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(this, "rthl")
            .setContentTitle("Subscribed to the Notifications")
            .setContentText("Service is running...")
            .setSmallIcon(R.drawable.ic_build)
            .build()

        startForeground(1, notification)

        with(NotificationManagerCompat.from(this)) {
            cancel(1)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}