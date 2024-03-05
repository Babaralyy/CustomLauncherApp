package com.codecoy.balancelauncherapp.callbacks

import com.codecoy.balancelauncherapp.data.model.NotificationData
import java.util.ArrayList

interface NotificationPostedRemoveCallback {
    fun onNotificationAdd(receivedList: ArrayList<NotificationData>?)

    fun onNotificationRemove(receivedList: ArrayList<NotificationData>?)
}
