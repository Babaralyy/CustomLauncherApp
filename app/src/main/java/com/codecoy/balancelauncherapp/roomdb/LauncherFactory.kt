package com.codecoy.balancelauncherapp.roomdb

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LauncherFactory(private val launcherRepository: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LauncherViewModel(launcherRepository) as T
    }
}
