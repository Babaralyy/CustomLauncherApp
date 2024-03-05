package com.codecoy.balancelauncherapp.appblock

import android.os.Parcel
import android.os.Parcelable

data class BlockWebDataModel(var url: String? = null, var packageName: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
    ) {
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(url)
        parcel.writeString(packageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlockWebDataModel> {
        override fun createFromParcel(parcel: Parcel): BlockWebDataModel {
            return BlockWebDataModel(parcel)
        }

        override fun newArray(size: Int): Array<BlockWebDataModel?> {
            return arrayOfNulls(size)
        }
    }
}
