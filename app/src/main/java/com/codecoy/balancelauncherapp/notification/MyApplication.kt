package com.codecoy.balancelauncherapp.notification

import android.app.Application
import android.util.Log
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "MyApplication:: onCreate")

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("blanace").addOnSuccessListener {
            Log.i(TAG, "MyApplication:: Success")
        }.addOnFailureListener {
            Log.i(TAG, "MyApplication:: failure ${it.message}")
        }
    }
}
