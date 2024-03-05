package com.codecoy.balancelauncherapp.roomdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BlockedAppsEntity")
data class BlockedAppsEntity(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "app_name") var appName: String? = null,
    @ColumnInfo(name = "package_name") var packageName: String? = null,
    @ColumnInfo(name = "is_blocked") var isblocked: Boolean? = false,
)
