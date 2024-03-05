package com.codecoy.balancelauncherapp.roomdb

import android.app.Application
import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedAppsEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedWebsiteEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlocksCatsEntity

/**
 * Created by Usman on 22,September,2021
 */
class LauncherRepository(application: Application) {
    private val mDBDao: LauncherDao

    init {
        val db: LauncherRoomDatabase = LauncherRoomDatabase.getDatabase(application.applicationContext)
        mDBDao = db.db_dao()
    }

    fun insertBlockedData(blockedAppsEntity: BlockedAppsEntity) {
        LauncherRoomDatabase.databaseWriteExecutor.execute { mDBDao.insertBlockedData(blockedAppsEntity) }
    }

    fun insertBlockedCatData(blocksCatsEntity: BlocksCatsEntity) {
        LauncherRoomDatabase.databaseWriteExecutor.execute { mDBDao.insertBlockedCatData(blocksCatsEntity) }
    }

    fun insertBlockedWebData(blockedWebsiteEntity: BlockedWebsiteEntity) {
        LauncherRoomDatabase.databaseWriteExecutor.execute { mDBDao.insertBlockedWebData(blockedWebsiteEntity) }
    }

    fun insertAppData(appsDataEntity: AppsDataEntity)  {
        LauncherRoomDatabase.databaseWriteExecutor.execute { mDBDao.insertAppData(appsDataEntity) }
    }

    // for managing questions in database
    fun checkAppByPackageName(packageName: String): Int {
        return mDBDao.checkAppByPackageName(packageName)
    }

    fun checkCatByPackageName(packageName: String): Int {
        return mDBDao.checkCatByPackageName(packageName, true)
    }

    fun updateBlockedData(blockedAppsEntity: BlockedAppsEntity) {
        LauncherRoomDatabase.databaseWriteExecutor.execute { mDBDao.updateBlockedData(blockedAppsEntity) }
    }

//    val getAllBlockedDataList: LiveData<List<BlockedAppsEntity>>
//        get() = mDBDao.getAllBlockedDataList()

    fun getAllBlockedDataList() = mDBDao.getAllBlockedDataList()

    fun getAllBlockedCatsList() = mDBDao.getAllBlockedCatsList()

    fun getAllBlockedWebList() = mDBDao.getAllBlockedWebList()

    fun getAllAppsList() = mDBDao.getAllAppsList()

    fun deleteAllBlockedApps() {
        mDBDao.deleteAllBlockedApps()
    }

    fun deleteAllBlockedCats() {
        mDBDao.deleteAllBlockedCats()
    }

    fun deleteAllBlockedWebsites() {
        mDBDao.deleteAllBlockedWebsites()
    }
}
