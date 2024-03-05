package com.codecoy.balancelauncherapp.callbacks

import com.codecoy.balancelauncherapp.databinding.InstallAppLayBinding

interface AppLauncherCallback {
    fun launchApp(packageName: String)

    fun manageApp(
        packageName: String,
        mBinding: InstallAppLayBinding,
    )
}
