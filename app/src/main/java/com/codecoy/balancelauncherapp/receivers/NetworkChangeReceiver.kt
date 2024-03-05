package com.codecoy.balancelauncherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.codecoy.balancelauncherapp.callbacks.NetworkCallback

class NetworkChangeReceiver(private var networkCallback: NetworkCallback) : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected

        if (isConnected) {
            // Connected to the internet
            networkCallback.onConnected()
        } else {
            // Not connected to the internet
            networkCallback.onDisconnected()
        }
    }
}
