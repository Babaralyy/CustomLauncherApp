package com.codecoy.balancelauncherapp.appblock.receivers

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.ui.activities.ScreenBlocker
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import com.codecoy.balancelauncherapp.utils.Utils.getLauncherTopApp

class AppLockReceiver : BroadcastReceiver() {
    private fun killAppByPackageName(
        context: Context,
        packageName: String,
    ) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageList = am.runningAppProcesses
        if (packageList != null) {
            for (appProcess in packageList) {
                if (appProcess.processName == packageName) {
                    try {
                        am.killBackgroundProcesses(packageName)
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onReceive(
        context: Context,
        p1: Intent?,
    ) {
        val appRunning: String = getLauncherTopApp(context)

        Log.i(TAG, "getLauncherTopApp:: $appRunning")

        val blockApp =
            LauncherRoomDatabase.getDatabase(context).db_dao().checkAppByPackageName(appRunning)
        val blockCat =
            LauncherRoomDatabase.getDatabase(context).db_dao()
                .checkCatByPackageName(appRunning, true)

        // always BLOCK
        if (blockApp >= 1 || blockCat >= 1) {
            killAppByPackageName(
                context,
                appRunning,
            )
            val i = Intent(context, ScreenBlocker::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.putExtra("broadcast_receiver", "broadcast_receiver")
            context.startActivity(i)
        }
//        else if (appRunning == "com.google.android.packageinstaller"){
//            val i = Intent(context, LockPatternActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            i.putExtra("broadcast_receiver", "broadcast_receiver")
//            context.startActivity(i)
//        }
    }
}
