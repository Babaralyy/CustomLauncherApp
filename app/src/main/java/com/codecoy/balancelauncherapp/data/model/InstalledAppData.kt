package com.codecoy.balancelauncherapp.data.model

data class InstalledAppData(
    var appName: String? = null,
    var packageName: String? = null,
    var versionName: String? = null,
    var versionCode: String? = null,
    var isSystemApp: Boolean = false,
    var isActiveNot: Boolean = false,
)
