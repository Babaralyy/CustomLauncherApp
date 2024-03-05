package com.codecoy.balancelauncherapp.utils

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import android.util.Log

object Utils {
    const val TAG = "Launcher"

    const val productId = "balance_access"
    const val productSubId = "balance_subscription"

    var isReadyToUninstall = true
    var browserName: String = "https://www.google.com"
    var passCode: String = ""

    private lateinit var usageStatsManager: UsageStatsManager

    private lateinit var sharedPreferences: SharedPreferences

    fun getLauncherTopApp(context: Context): String {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val taskInfoList = manager.getRunningTasks(1)
            if (null != taskInfoList && taskInfoList.isNotEmpty()) {
                return taskInfoList[0].topActivity!!.packageName
            }
        } else {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            var result = ""
            val event = UsageEvents.Event()
            val usageEvents: UsageEvents = usageStatsManager.queryEvents(beginTime, endTime)
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.packageName
                }
            }
            if (!TextUtils.isEmpty(result)) Log.d("RESULT", result)
            return result
        }
        return ""
    }

    fun saveGetStartedIntoPref(
        context: Context,
        isStarted: Boolean,
    )  {
        sharedPreferences = context.getSharedPreferences("isStarted", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("iStarted", isStarted)
        editor.apply()
    }

    fun fetchGetStartedFromPref(context: Context): Boolean  {
        sharedPreferences = context.getSharedPreferences("isStarted", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("iStarted", false)
    }

    fun saveOnBoardIntoPref(
        context: Context,
        isOnBoard: Boolean,
    )  {
        sharedPreferences = context.getSharedPreferences("isOnBoard", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("iOnBoard", isOnBoard)
        editor.apply()
    }

    fun fetchOnBoardFromPref(context: Context): Boolean  {
        sharedPreferences = context.getSharedPreferences("isOnBoard", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("iOnBoard", false)
    }

    fun savePasscode(
        context: Context,
        passCode: String,
    )  {
        sharedPreferences = context.getSharedPreferences("passCode", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("pCode", passCode)
        editor.apply()
    }

    fun fetchPasscode(context: Context): String?  {
        sharedPreferences = context.getSharedPreferences("passCode", Context.MODE_PRIVATE)
        return sharedPreferences.getString("pCode", null)
    }

    fun saveConfirmPasscode(
        context: Context,
        confirmPasscode: String,
    )  {
        sharedPreferences = context.getSharedPreferences("confirmPasscode", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("cPasscode", confirmPasscode)
        editor.apply()
    }

    fun fetchConfirmPasscode(context: Context): String?  {
        sharedPreferences = context.getSharedPreferences("confirmPasscode", Context.MODE_PRIVATE)
        return sharedPreferences.getString("cPasscode", null)
    }

    fun savePaymentIntoPref(
        context: Context,
        isPayed: Boolean,
    )  {
        sharedPreferences = context.getSharedPreferences("isPayed", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("iPayed", isPayed)
        editor.apply()
    }

    fun fetchPaymentFromPref(context: Context): Boolean  {
        sharedPreferences = context.getSharedPreferences("isPayed", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("iPayed", false)
    }

    fun saveSubIntoPref(
        context: Context,
        isSubscribed: Boolean,
    )  {
        sharedPreferences = context.getSharedPreferences("isSubscribed", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("iSubscribed", isSubscribed)
        editor.apply()
    }

    fun fetchSubFromPref(context: Context): Boolean  {
        sharedPreferences = context.getSharedPreferences("isSubscribed", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("iSubscribed", false)
    }

    fun saveRecoveryEmail(
        context: Context,
        recoveryEmail: String,
    )  {
        sharedPreferences = context.getSharedPreferences("recoveryEmail", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("rEmail", recoveryEmail)
        editor.apply()
    }

    fun fetchRecoveryEmail(context: Context): String?  {
        sharedPreferences = context.getSharedPreferences("recoveryEmail", Context.MODE_PRIVATE)
        return sharedPreferences.getString("rEmail", null)
    }

    fun saveRestrictedIntoPref(
        context: Context,
        isStarted: Boolean,
    )  {
        sharedPreferences = context.getSharedPreferences("isRestricted", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("iRestricted", isStarted)
        editor.apply()
    }

    fun fetchRestrictedFromPref(context: Context): Boolean  {
        sharedPreferences = context.getSharedPreferences("isRestricted", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("iRestricted", false)
    }
}
