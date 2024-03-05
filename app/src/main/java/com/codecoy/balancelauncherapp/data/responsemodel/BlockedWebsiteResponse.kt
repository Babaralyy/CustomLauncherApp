package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedWebsiteResponse(
    @SerializedName("data") var data: ArrayList<BlockedWebsiteData> = arrayListOf(),
    @SerializedName("meta") var meta: Meta? = Meta(),
)
