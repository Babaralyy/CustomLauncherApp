package com.codecoy.balancelauncherapp.notification

import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data // Access message data
        val notification = remoteMessage.notification // Access notification data (if any)

        val intent = Intent("com.codecoy.balancelauncherapp.notification.MESSAGE")

        intent.putExtra("onMessageReceived", data["notificationType"])
        sendBroadcast(intent)

        Log.i(TAG, "FirebaseMessagingService:: $data  ${notification?.body}")
    }
}
