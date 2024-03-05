package com.codecoy.balancelauncherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.data.model.NotificationData
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.utils.Utils.TAG

class NotificationPostedRemoveReceiver() : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        // Handle the broadcast here
        if (intent?.action == "com.codecoy.balancelauncherapp.notification.onNotificationPosted") {
            Log.i(TAG, "StatusBarNotification:: onNotificationPosted Receiver")

            if (intent.extras != null)
                {
                    val receivedObj = intent.getParcelableExtra<NotificationData>("custom_objects_notificationsList")

                    if (receivedObj != null)
                        {
                            val objToUpdate =
                                LauncherRoomDatabase.getDatabase(
                                    context,
                                ).db_dao().getNotCount(receivedObj.packageName.toString())

                            if (objToUpdate != null)
                                {
                                    val totalCount = objToUpdate.notCount + 1
                                    LauncherRoomDatabase.getDatabase(
                                        context,
                                    ).db_dao().updateNotCount(totalCount, receivedObj.packageName.toString())
                                }
                        }
                }
        } else if (intent?.action == "com.codecoy.balancelauncherapp.notification.onNotificationRemoved")
            {
                Log.i(TAG, "StatusBarNotification:: onNotificationRemoved Receiver")

                if (intent.extras != null)
                    {
                        val receivedObj = intent.getParcelableExtra<NotificationData>("custom_objects_notificationsList")

                        if (receivedObj != null)
                            {
                                val objToUpdate =
                                    LauncherRoomDatabase.getDatabase(
                                        context,
                                    ).db_dao().getNotCount(receivedObj.packageName.toString())

                                if (objToUpdate != null)
                                    {
                                        if (objToUpdate.notCount > 0)
                                            {
                                                val totalCount = objToUpdate.notCount - 1
                                                LauncherRoomDatabase.getDatabase(
                                                    context,
                                                ).db_dao().updateNotCount(totalCount, receivedObj.packageName.toString())
                                            }
                                    }
                            }
                    }
            }
    }
}
