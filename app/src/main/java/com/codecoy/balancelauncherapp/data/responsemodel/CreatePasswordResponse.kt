package com.codecoy.balancelauncherapp.data.responsemodel

import com.google.gson.annotations.SerializedName

data class CreatePasswordResponse(
    @SerializedName("data") var data: CreatePasswordData? = CreatePasswordData(),
)
