package com.codecoy.balancelauncherapp.roomdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BlockedWebsiteEntity")
class BlockedWebsiteEntity(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "app_name") var webDomain: String? = null,
    @ColumnInfo(name = "is_blocked") var isBlocked: Boolean? = false,
)
