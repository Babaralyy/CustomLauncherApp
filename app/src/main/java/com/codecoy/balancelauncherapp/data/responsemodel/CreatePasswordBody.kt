package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class CreatePasswordBody(
    @SerializedName("email") var email: String? = null,
    @SerializedName("passcode") var passcode: String? = null,
    @SerializedName("isCreated") var isCreated: String? = null,
)
