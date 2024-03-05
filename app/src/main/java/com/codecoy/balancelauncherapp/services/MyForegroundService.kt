package com.codecoy.balancelauncherapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import java.util.Timer

class MyForegroundService : Service() {
    private val channelId = "ForegroundServiceChannel"
    private lateinit var usageStatsManager: UsageStatsManager
    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        // Perform any initialization here
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        // Start the service in the foreground
        createNotificationChannel()

        val notification: Notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Balance")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.settings_suggest) // Replace with your icon
                .build()

        startForeground(1, notification)

        // Start the Accessibility Service
        val accessibilityIntent = Intent(applicationContext, MyForegroundService::class.java)
        applicationContext.startService(accessibilityIntent)

        return START_STICKY
    }

    private fun topApp() {
        getLauncherTopApp(this)
    }

    private fun getLauncherTopApp(context: Context): String {
        usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents: UsageEvents = usageStatsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        if (!TextUtils.isEmpty(result)) Log.d("RESULT", result)
        Log.i(TAG, "onStartCommand:: $result")
        return result
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources or perform necessary tasks when the service is stopped
    }

    private fun createNotificationChannel() {
        val serviceChannel =
            NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
}
