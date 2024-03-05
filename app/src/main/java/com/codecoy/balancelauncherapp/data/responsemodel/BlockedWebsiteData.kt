package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class BlockedWebsiteData(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("domain") var domain: String? = null,
    @SerializedName("isBlocked") var isBlocked: Boolean? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null,
)
