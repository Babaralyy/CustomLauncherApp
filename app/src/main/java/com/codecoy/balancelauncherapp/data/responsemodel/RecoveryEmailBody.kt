package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class RecoveryEmailBody(
    @SerializedName("email") var email: String? = null,
    @SerializedName("isNew") var isNew: String? = null,
)
