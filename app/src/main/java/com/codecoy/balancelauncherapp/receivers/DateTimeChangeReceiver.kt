package com.codecoy.balancelauncherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.callbacks.DateAndTimeChangeCallback

class DateTimeChangeReceiver(private val dateAndTimeChangeCallback: DateAndTimeChangeCallback) : BroadcastReceiver() {
    private val tag = "DateTimeChangeReceiver"

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        Log.i(tag, "onReceive:: onReceive")

        if (intent?.action == Intent.ACTION_TIME_TICK) {
            dateAndTimeChangeCallback.onTimeChanged()
        } else if (intent?.action == Intent.ACTION_DATE_CHANGED) {
            dateAndTimeChangeCallback.onDateChanged()
            Log.i(tag, "onReceive:: Date changed")
        }
    }
}
