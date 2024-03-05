package com.codecoy.balancelauncherapp.services.notificationlistener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.codecoy.balancelauncherapp.common.Constants.Companion.APP_PACKAGE_NAME
import com.codecoy.balancelauncherapp.data.model.NotificationData
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase

class MyNotificationListenerService : NotificationListenerService() {
    private val seenNotifications = mutableSetOf<String>()
    private val removedNotifications = mutableSetOf<String>()
    private val notificationsList = arrayListOf<NotificationData>()
    private val tag = "NotificationListenerCallback"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val id = sbn.id
        val notificationId = sbn.key
        val packageName = sbn.packageName

        val notification = sbn.notification
        val contentIntent = notification.contentIntent

        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()

        if (!seenNotifications.contains(notificationId)) {
            seenNotifications.add(notificationId)

            if (!packageName.equals(APP_PACKAGE_NAME)) {
                Log.i(tag, "StatusBarNotification:: onNotificationPosted called $packageName")

                val notObj = NotificationData(notificationId, id, packageName, appName)

                val objToUpdate = LauncherRoomDatabase.getDatabase(this).db_dao().getNotCount(notObj.packageName.toString())

                if (objToUpdate != null)
                    {
                        val totalCount = objToUpdate.notCount + 1
                        LauncherRoomDatabase.getDatabase(this).db_dao().updateNotCount(totalCount, notObj.packageName.toString())
                    }

//                val intent =
//                    Intent("com.codecoy.balancelauncherapp.notification.onNotificationPosted")
//                intent.putExtra("custom_objects_notificationsList", notObj)
//                sendBroadcast(intent)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)

        val id = sbn.id
        val notificationId = sbn.key
        val packageName = sbn.packageName
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()

        notificationsList.removeIf { it.notificationId == notificationId }

        Log.i(tag, "StatusBarNotification:: onNotificationRemoved called")

        if (!removedNotifications.contains(notificationId)) {
            removedNotifications.add(notificationId)

            val notObj = NotificationData(notificationId, id, packageName, appName)

            val objToUpdate = LauncherRoomDatabase.getDatabase(this).db_dao().getNotCount(notObj.packageName.toString())

            if (objToUpdate != null)
                {
                    if (objToUpdate.notCount > 0)
                        {
                            val totalCount = objToUpdate.notCount - 1
                            LauncherRoomDatabase.getDatabase(this).db_dao().updateNotCount(totalCount, notObj.packageName.toString())
                        }
                }

//            val intent = Intent("com.codecoy.balancelauncherapp.notification.onNotificationRemoved")
//            intent.putExtra("custom_objects_notificationsList", notObj)
//            sendBroadcast(intent)
        }
    }
}
