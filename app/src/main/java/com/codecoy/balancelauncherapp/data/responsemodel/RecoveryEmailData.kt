package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class RecoveryEmailData(
    @SerializedName("email") var email: String? = null,
    @SerializedName("passcode") var passcode: String? = null,
    @SerializedName("_id") var id: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null,
)
