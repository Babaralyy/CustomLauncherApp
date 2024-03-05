package com.codecoy.balancelauncherapp.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(
        context: Context,
        intent: Intent,
    ) {
        super.onEnabled(context, intent)
        // This method is called when the app is granted device admin privileges.
    }

    override fun onDisabled(
        context: Context,
        intent: Intent,
    ) {
        // This method is called when the app's device admin privileges are disabled.
        super.onDisabled(context, intent)
    }
}
