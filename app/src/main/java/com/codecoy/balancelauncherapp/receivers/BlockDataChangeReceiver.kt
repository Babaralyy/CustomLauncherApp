package com.codecoy.balancelauncherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.callbacks.ChangeBlockDataCallback
import com.codecoy.balancelauncherapp.utils.Utils

class BlockDataChangeReceiver(private var changeBlockDataCallback: ChangeBlockDataCallback) : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        // Handle the broadcast here
        if (intent?.action == "com.codecoy.balancelauncherapp.notification.MESSAGE") {
            Log.i(Utils.TAG, "FirebaseMessagingService:: BlockDataChangeReceiver")

            if (intent.extras != null)
                {
                    val extras = intent.extras

                    when (extras?.getString("onMessageReceived")) {
                        "app" -> {
                            changeBlockDataCallback.onAppBlockUnblock()
                        }
                        "category" -> {
                            changeBlockDataCallback.onCatBlockUnblock()
                        }
                        "website" -> {
                            changeBlockDataCallback.onWebBlockUnblock()
                        }
                    }
                }
        }
    }
}
