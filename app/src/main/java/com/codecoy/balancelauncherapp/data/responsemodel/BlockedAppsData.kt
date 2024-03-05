package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedAppsData(
    @SerializedName("apps") var apps: ArrayList<BlockedApps> = arrayListOf(),
)
