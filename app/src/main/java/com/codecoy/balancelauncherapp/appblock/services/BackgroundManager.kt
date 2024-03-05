package com.codecoy.balancelauncherapp.appblock.services

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.codecoy.balancelauncherapp.appblock.receivers.RestartServiceWhenStopped

class BackgroundManager private constructor() {
    private val period = 15 * 1000L
    private val alarmId = 159874
    private var context: Context? = null

    companion object {
        private var instance: BackgroundManager? = null

        fun getInstance(): BackgroundManager {
            if (instance == null) {
                instance = BackgroundManager()
            }
            return instance!!
        }

//        fun getInstance(): BackgroundManager {
//            return instance ?: BackgroundManager().apply { instance = this }
//        }
    }

    fun init(ctx: Context): BackgroundManager {
        context = ctx
        return this
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = this.context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        manager?.let {
            for (serviceInfo in it.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == serviceInfo.service.className) {
                    return true
                }
            }
        }
        return false
    }

    fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(ServiceAppLockJobIntent::class.java)) {
                val intent = Intent(context, ServiceAppLockJobIntent::class.java)
                ServiceAppLockJobIntent.enqueueWork(context!!, intent)
            }
        } else {
            if (!isServiceRunning(ServiceAppLock::class.java)) {
                context?.startService(Intent(context, ServiceAppLock::class.java))
            }
        }
    }

//    fun startService() {
//        val serviceClass =
//            ServiceAppLockJobIntent::class.java
//
//        if (!isServiceRunning(serviceClass, context)) {
//            val intent = Intent(context, serviceClass)
//            ServiceAppLockJobIntent.enqueueWork(context!!, intent)
//        }
//    }

    fun stopService(serviceClass: Class<*>) {
        if (isServiceRunning(serviceClass)) {
            context?.let {
                it.stopService(Intent(it, serviceClass))
            }
        }
    }

    fun startAlarmManager() {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        manager?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period, pendingIntent)
    }

    fun stopAlarm() {
        val intent = Intent(context, RestartServiceWhenStopped::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        manager?.let {
            it.cancel(pendingIntent)
        }
    }
}
