package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedCatResponse(
    @SerializedName("data") var data: ArrayList<BlockedCatData> = arrayListOf(),
    @SerializedName("meta") var meta: Meta? = Meta(),
)
