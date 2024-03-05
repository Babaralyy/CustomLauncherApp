package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class CheckEmailBody(
    @SerializedName("email") var email: String? = null,
)
