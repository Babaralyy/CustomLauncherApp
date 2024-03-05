package com.codecoy.balancelauncherapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.codecoy.balancelauncherapp.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

object Constant {

     const val EMAIL = "balance@launcher.com"
     const val PASSWORD = "balance_@143"

    //    private const val BASE_URL = "http://93.188.167.72/balance/api/v1/"
    private const val BASE_URL = "http://217.196.50.103/balance/api/v1/"


    private var httpClient: OkHttpClient =
        OkHttpClient.Builder()
            .hostnameVerifier { _, _ ->
                HttpsURLConnection.getDefaultHostnameVerifier()
                true
            }
            .connectTimeout(30000, TimeUnit.SECONDS)
            .readTimeout(30000, TimeUnit.SECONDS)
            .writeTimeout(30000, TimeUnit.SECONDS).build()

    private val gson: Gson =
        GsonBuilder()
            .setLenient()
            .create()

    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient).build()
    }

    fun getDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_lay)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
