package com.codecoy.balancelauncherapp.roomdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppsDataEntity")
data class AppsDataEntity(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "app_name") var appName: String? = null,
    @ColumnInfo(name = "package_name") var packageName: String? = null,
    @ColumnInfo(name = "is_system_app") var isSystemApp: Boolean? = false,
    @ColumnInfo(name = "not_count") var notCount: Int = 0,
)
