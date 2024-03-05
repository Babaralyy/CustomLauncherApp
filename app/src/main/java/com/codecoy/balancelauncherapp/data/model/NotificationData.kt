package com.codecoy.balancelauncherapp.data.model

import android.os.Parcel
import android.os.Parcelable

data class NotificationData(var notificationId: String? = null, var id: Int, var packageName: String? = null, var appName: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
    ) {
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(notificationId)
        parcel.writeInt(id)
        parcel.writeString(packageName)
        parcel.writeString(appName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationData> {
        override fun createFromParcel(parcel: Parcel): NotificationData {
            return NotificationData(parcel)
        }

        override fun newArray(size: Int): Array<NotificationData?> {
            return arrayOfNulls(size)
        }
    }
}
