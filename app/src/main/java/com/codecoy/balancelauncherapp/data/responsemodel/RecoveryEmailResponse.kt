package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class RecoveryEmailResponse(
    @SerializedName("data") var data: RecoveryEmailData? = RecoveryEmailData(),
)
