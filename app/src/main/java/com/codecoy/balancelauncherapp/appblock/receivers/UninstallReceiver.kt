package com.codecoy.balancelauncherapp.appblock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.codecoy.balancelauncherapp.ui.activities.LockPatternActivity

class UninstallReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        val i = Intent(context, LockPatternActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(i)
    }
}
