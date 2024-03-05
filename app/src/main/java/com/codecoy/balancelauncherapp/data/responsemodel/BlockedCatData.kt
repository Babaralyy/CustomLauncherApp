package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedCatData(
    @SerializedName("packageName") var packageName: String? = null,
    @SerializedName("isBlocked") var isBlocked: Boolean? = null,
)
