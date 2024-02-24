package me.theclashfruit.rithle.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.modrinth.Modrinth
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationService : Service() {
    private val QUERY_INTERVAL = TimeUnit.HOURS.toMillis(1)

    private val modrinthApi = Modrinth()

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()

        handler = Handler()

        runnable = Runnable {
            queryNotifications()

            handler.postDelayed(runnable, QUERY_INTERVAL)
        }

        // Start the first API query
        handler.postDelayed(runnable, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(runnable)
    }

    @SuppressLint("MissingPermission")
    private fun queryNotifications() {
        modrinthApi.loggedInUser { user ->
            modrinthApi.userNotifications(user) {
                it.forEach { notification ->
                    Log.d("NotificationService", notification.toString())
                    Log.d("NotificationService", it.size.toString())

                    if (/* !notification.read!! && */ notification.body!!.type != "legacy_markdown") {
                        when (notification.body.type) {
                            "team_invite" -> {
                                modrinthApi.user(notification.body.invited_by!!) { user ->
                                    Log.d("NotificationService", user.toString())

                                    val notif = NotificationCompat.Builder(this, "mr:${notification.type}")
                                        .setSmallIcon(R.drawable.ic_person_add)
                                        .setWhen(ZonedDateTime.parse(notification.created!!).toEpochSecond() * 1000)
                                        .setColor(getColor(R.color.colorPrimaryLight))

                                    notif.setContentTitle(notification.title)
                                    notif.setContentText("${user.username} has invited you to join ${notification.body.organization_id}.")

                                    if (notification.actions!!.isNotEmpty()) {
                                        notification.actions.forEach { action ->
                                            notif.addAction(
                                                NotificationCompat.Action.Builder(
                                                    R.drawable.ic_notifications,
                                                    action.title,
                                                    PendingIntent.getActivity(
                                                        this,
                                                        0,
                                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://modrinth.com/${action.actionRoute?.get(1)}")),
                                                        PendingIntent.FLAG_IMMUTABLE
                                                    )
                                                ).build()
                                            )
                                        }
                                    }

                                    with(NotificationManagerCompat.from(this)) {
                                        val codeArray = notification.id!!.toCharArray().map { char ->
                                            char.code
                                        }

                                        var num = 0

                                        codeArray.forEach { code ->
                                            num += code
                                        }

                                        Log.d("NotificationService", "SENDING")

                                        notify(num, notif.build())
                                    }
                                }
                            }
                            "organization_invite" -> {
                                modrinthApi.user(notification.body.invited_by!!) { user ->
                                    Log.d("NotificationService", user.toString())

                                    val notif = NotificationCompat.Builder(this, "mr:${notification.type}")
                                        .setSmallIcon(R.drawable.ic_person_add)
                                        .setWhen(ZonedDateTime.parse(notification.created!!).toEpochSecond() * 1000)
                                        .setColor(getColor(R.color.colorPrimaryLight))

                                    notif.setContentTitle(notification.title)
                                    notif.setContentText("${user.username} has invited you to join ${notification.body.organization_id}.")

                                    if (notification.actions!!.isNotEmpty()) {
                                        notification.actions.forEach { action ->
                                            notif.addAction(
                                                NotificationCompat.Action.Builder(
                                                    R.drawable.ic_notifications,
                                                    action.title,
                                                    PendingIntent.getActivity(
                                                        this,
                                                        0,
                                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://modrinth.com/${action.actionRoute?.get(1)}")),
                                                        PendingIntent.FLAG_IMMUTABLE
                                                    )
                                                ).build()
                                            )
                                        }
                                    }

                                    with(NotificationManagerCompat.from(this)) {
                                        val codeArray = notification.id!!.toCharArray().map { char ->
                                            char.code
                                        }

                                        var num = 0

                                        codeArray.forEach { code ->
                                            num += code
                                        }

                                        Log.d("NotificationService", "SENDING")

                                        notify(num, notif.build())
                                    }
                                }
                            }
                            "project_update" -> {
                                val notif = NotificationCompat.Builder(this, "mr:${notification.type}")
                                    .setSmallIcon(R.drawable.ic_update)
                                    .setWhen(ZonedDateTime.parse(notification.created!!).toEpochSecond() * 1000)
                                    .setColor(getColor(R.color.colorPrimaryLight))

                                notif.setContentTitle(notification.title)
                                notif.setContentText(notification.text)

                                if (notification.actions!!.isNotEmpty()) {
                                    notification.actions.forEach { action ->
                                        notif.addAction(
                                            NotificationCompat.Action.Builder(
                                                R.drawable.ic_notifications,
                                                action.title,
                                                PendingIntent.getActivity(
                                                    this,
                                                    0,
                                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://modrinth.com/${action.actionRoute?.get(1)}")),
                                                    PendingIntent.FLAG_IMMUTABLE
                                                )
                                            ).build()
                                        )
                                    }
                                }

                                with(NotificationManagerCompat.from(this)) {
                                    val codeArray = notification.id!!.toCharArray().map { char ->
                                        char.code
                                    }

                                    var num = 0

                                    codeArray.forEach { code ->
                                        num += code
                                    }

                                    Log.d("NotificationService", "SENDING")

                                    notify(num, notif.build())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}