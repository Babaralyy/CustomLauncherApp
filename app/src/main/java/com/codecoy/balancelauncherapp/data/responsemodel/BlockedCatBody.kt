package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedCatBody(
    @SerializedName("apps") var apps: ArrayList<String> = arrayListOf(),
)
