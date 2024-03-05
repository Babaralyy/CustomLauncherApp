package com.codecoy.balancelauncherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.appblock.services.BackgroundManager
import com.codecoy.balancelauncherapp.utils.Utils.TAG

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            try {
                context?.let { BackgroundManager.getInstance().init(it).startService() }
            } catch (e: Exception) {
                Log.i(TAG, "BootCompletedReceiver:: Exception ${e.message}")
            }
        }
    }
}
