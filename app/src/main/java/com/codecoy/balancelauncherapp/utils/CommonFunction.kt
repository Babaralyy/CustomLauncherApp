package com.codecoy.balancelauncherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.isNetworkConnected(): Boolean {
    val cm =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
}
