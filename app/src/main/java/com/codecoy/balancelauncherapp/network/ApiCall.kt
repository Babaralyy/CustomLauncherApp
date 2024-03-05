package com.codecoy.balancelauncherapp.network

import com.codecoy.balancelauncherapp.data.responsemodel.BlockedAppsResponse
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatBody
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatResponse
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedWebsiteResponse
import com.codecoy.balancelauncherapp.data.responsemodel.CheckEmailBody
import com.codecoy.balancelauncherapp.data.responsemodel.CheckEmailResponse
import com.codecoy.balancelauncherapp.data.responsemodel.CreatePasswordBody
import com.codecoy.balancelauncherapp.data.responsemodel.CreatePasswordResponse
import com.codecoy.balancelauncherapp.data.responsemodel.RecoveryEmailBody
import com.codecoy.balancelauncherapp.data.responsemodel.RecoveryEmailResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiCall {
    @GET("app/list")
    fun getAllBlockedApps(): Call<BlockedAppsResponse>

    @POST("app/check-category")
    fun getAllBlockedCategories(
        @Body blockedCatBody: BlockedCatBody,
    ): Call<BlockedCatResponse>

    @GET("website")
    fun getAllBlockedWebsites(): Call<BlockedWebsiteResponse>

    @POST("passcode/email")
    fun addRecoveryEmail(
        @Body recoveryEmailBody: RecoveryEmailBody,
    ): Call<RecoveryEmailResponse>

    @POST("passcode/code")
    fun addPassword(
        @Body createPasswordBody: CreatePasswordBody,
    ): Call<CreatePasswordResponse>

    @POST("passcode/check")
    fun checkEmail(
        @Body checkEmailBody: CheckEmailBody,
    ): Call<CheckEmailResponse>
}
