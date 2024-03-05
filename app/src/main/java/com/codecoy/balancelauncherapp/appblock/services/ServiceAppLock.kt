package com.codecoy.balancelauncherapp.appblock.services

import android.app.IntentService
import android.content.Intent
import com.codecoy.balancelauncherapp.appblock.receivers.AppLockReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServiceAppLock : IntentService("ServiceAppLock") {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @OptIn(DelicateCoroutinesApi::class)
    private fun runAppLock() {
        val endTime = System.currentTimeMillis() + 210
        while (System.currentTimeMillis() < endTime) {
            synchronized(this) {
                try {
                    val intent = Intent(this, AppLockReceiver::class.java)

                    sendBroadcast(intent)
                    GlobalScope.launch {
                        endTime - System.currentTimeMillis()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

//    private fun runAppLock() {
//        val endTime = System.currentTimeMillis() + 210
//
//        coroutineScope.launch {
//            while (System.currentTimeMillis() < endTime) {
//                try {
//                    val intent = Intent(this@ServiceAppLock, AppLockReceiver::class.java)
//                    sendBroadcast(intent)
//
//                    // You may want to delay the next iteration
//                    delay(100) // Adjust the delay time as needed
//
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        runAppLock()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.getInstance().init(this).startService()
        BackgroundManager.getInstance().init(this).startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onHandleIntent(intent: Intent?) {
        // Handle the intent here
    }

    override fun onDestroy() {
        BackgroundManager.getInstance().init(this).startService()
        BackgroundManager.getInstance().init(this).startAlarmManager()
        super.onDestroy()
    }
}
