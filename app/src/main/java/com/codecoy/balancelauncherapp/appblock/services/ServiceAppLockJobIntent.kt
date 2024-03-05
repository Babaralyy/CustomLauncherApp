package com.codecoy.balancelauncherapp.appblock.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.codecoy.balancelauncherapp.appblock.receivers.AppLockReceiver
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServiceAppLockJobIntent : JobIntentService() {

    companion object {
        private const val JOB_ID = 15462

        fun enqueueWork(
            ctx: Context,
            work: Intent,
        ) {
            enqueueWork(ctx, ServiceAppLockJobIntent::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        runAppLock()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        performCleanup()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        performCleanup()
        super.onDestroy()
    }

    private fun performCleanup() {
        val backgroundManager = BackgroundManager.getInstance().init(this)
        backgroundManager.startService()
        backgroundManager.startAlarmManager()
    }

//    private fun runAppLock() {
//        val durationMillis = 210L
//
//        // Using structured concurrency with lifecycleScope
//
//        launch {
//            delay(durationMillis)
//
//            // Once the delay is over, execute the task
//            val intent = Intent(this@ServiceAppLockJobIntent, AppLockReceiver::class.java)
//            sendBroadcast(intent)
//        }
//    }

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
}
