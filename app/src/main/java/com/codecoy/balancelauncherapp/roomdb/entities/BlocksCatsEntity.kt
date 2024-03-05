package com.codecoy.balancelauncherapp.roomdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BlocksCatsEntity")
class BlocksCatsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "package_name") var packageName: String? = null,
    @ColumnInfo(name = "is_blocked") var isblocked: Boolean? = false,
)
