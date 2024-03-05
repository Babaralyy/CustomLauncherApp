package com.codecoy.balancelauncherapp.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedAppsEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedWebsiteEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlocksCatsEntity

@Dao
interface LauncherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlockedData(blockedAppsEntity: BlockedAppsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlockedCatData(blocksCatsEntity: BlocksCatsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlockedWebData(blockedWebsiteEntity: BlockedWebsiteEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAppData(appsDataEntity: AppsDataEntity)

    @Query("SELECT * FROM BlockedAppsEntity")
    fun getAllBlockedDataList(): LiveData<List<BlockedAppsEntity>>

    @Query("SELECT * FROM BlocksCatsEntity")
    fun getAllBlockedCatsList(): LiveData<List<BlocksCatsEntity>>

    @Query("SELECT * FROM BlockedWebsiteEntity")
    fun getAllBlockedWebList(): MutableList<BlockedWebsiteEntity>

    @Query("SELECT app_name FROM BlockedWebsiteEntity")
    fun getAllBlockedWebListString(): MutableList<String>

    @Query("SELECT * FROM AppsDataEntity")
    fun getAllAppsList(): LiveData<MutableList<AppsDataEntity>>

    @Query("SELECT * FROM AppsDataEntity")
    fun getAllAppsDataList(): MutableList<AppsDataEntity>

    @Query("SELECT * FROM AppsDataEntity WHERE package_name = :packageName")
    fun getNotCount(packageName: String): AppsDataEntity

    @Query("UPDATE AppsDataEntity SET not_count = :notCount WHERE package_name =:packageName")
    fun updateNotCount(
        notCount: Int,
        packageName: String,
    )

    @Query("SELECT COUNT()  FROM BlockedAppsEntity WHERE package_name =:packageName")
    fun checkAppByPackageName(packageName: String): Int // return 0 or 1
    // if returns 1 then it means given id is exist in db

    @Query("SELECT COUNT()  FROM BlockedWebsiteEntity WHERE app_name =:webDomain")
    fun checkWebByDomain(webDomain: String): Int // return 0 or 1
    // if returns 1 then it means given id is exist in db

    @Query("SELECT EXISTS (SELECT * FROM BlockedWebsiteEntity WHERE app_name =:webDomain)")
    fun isWebBlocked(webDomain: String): Boolean

    @Query("SELECT COUNT()  FROM BlocksCatsEntity WHERE package_name =:packageName AND is_blocked =:isBlocked")
    fun checkCatByPackageName(
        packageName: String,
        isBlocked: Boolean,
    ): Int

    @Query("SELECT COUNT()  FROM BlockedAppsEntity WHERE package_name =:packageName")
    fun checkBlockedAppByPackageName(packageName: String): Int

    @Query("DELETE FROM BlockedAppsEntity")
    fun deleteAllBlockedApps()

    @Query("DELETE FROM BlocksCatsEntity")
    fun deleteAllBlockedCats()

    @Query("DELETE FROM BlockedWebsiteEntity")
    fun deleteAllBlockedWebsites()

    @Query("DELETE FROM AppsDataEntity")
    fun deleteAllAppsData()

    @Query("DELETE FROM AppsDataEntity WHERE package_name = :packageName")
    fun deleteAppData(packageName: String)

    @Update
    fun updateBlockedData(blockedAppsEntity: BlockedAppsEntity)
}
