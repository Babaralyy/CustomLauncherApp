package com.codecoy.balancelauncherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.callbacks.ManageAppCallback

class AppAddRemoveReceiver(private var manageAppCallback: ManageAppCallback) : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.encodedSchemeSpecificPart
                manageAppCallback.onAppInstalled(packageName)
                Log.i("AppAddRemoveReceiver", "App installed: $packageName")
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val packageName = intent.data?.encodedSchemeSpecificPart
                manageAppCallback.onAppRemoved(packageName)
                Log.i("AppAddRemoveReceiver", "App uninstalled: $packageName")
            }
        }
    }
}
