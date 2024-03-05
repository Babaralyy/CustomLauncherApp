package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class CheckEmailResponse(
    @SerializedName("data") var data: CheckEmailData? = CheckEmailData(),
)
