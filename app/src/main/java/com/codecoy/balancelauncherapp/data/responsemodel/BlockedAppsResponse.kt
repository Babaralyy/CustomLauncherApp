package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedAppsResponse(
    @SerializedName("data") var data: BlockedAppsData? = BlockedAppsData(),
    @SerializedName("meta") var meta: Meta? = Meta(),
)
