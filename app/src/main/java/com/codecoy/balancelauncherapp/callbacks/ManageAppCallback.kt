package com.codecoy.balancelauncherapp.callbacks

interface ManageAppCallback {
    fun onAppInstalled(packageName: String?)

    fun onAppRemoved(packageName: String?)
}
