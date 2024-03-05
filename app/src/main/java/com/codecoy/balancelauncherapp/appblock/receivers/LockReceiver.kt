package com.codecoy.balancelauncherapp.appblock.receivers

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.ui.activities.ScreenBlocker
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG

class LockReceiver : BroadcastReceiver() {
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
        intent: Intent?,
    ) {
        val appRunning: String = Utils.getLauncherTopApp(context)

        if (intent?.extras != null) {
            val extras = intent.extras
            val appOntOp = extras?.getString("appOnTop")

//            Log.i(TAG, "getLauncherTopApp:: $appOntOp")

            val blockApp =
                LauncherRoomDatabase.getDatabase(context).db_dao().checkAppByPackageName(appOntOp.toString())

            val blockCat =
                LauncherRoomDatabase.getDatabase(context).db_dao()
                    .checkCatByPackageName(appOntOp.toString(), true)

            // always BLOCK
            if (blockApp >= 1 || blockCat >= 1) {
                killAppByPackageName(
                    context,
                    appOntOp.toString(),
                )

                Log.i(TAG, "getLauncherTopApp:: blocked app--- $appOntOp")

                val i = Intent(context, ScreenBlocker::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(i)
            }

            /*if (appOntOp == "com.google.android.googlequicksearchbox") {

                killAppByPackageName(
                    context,
                    appOntOp.toString()
                )

                Log.i(TAG, "getLauncherTopApp:: blocked app else --- $appOntOp")

                val i = Intent(context, ScreenBlocker::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(i)
            }*/
        }
    }
}
