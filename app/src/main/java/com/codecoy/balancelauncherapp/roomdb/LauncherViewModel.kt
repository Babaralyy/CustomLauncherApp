package com.codecoy.balancelauncherapp.roomdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedAppsEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedWebsiteEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlocksCatsEntity

/**
 * Created by Usman on 22,September,2021
 */
class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: LauncherRepository

    init {
        mRepository = LauncherRepository(application)
    }

    fun insertBlockedData(blockedAppsEntity: BlockedAppsEntity) {
        mRepository.insertBlockedData(blockedAppsEntity)
    }

    fun insertBlockedCatData(blocksCatsEntity: BlocksCatsEntity) {
        mRepository.insertBlockedCatData(blocksCatsEntity)
    }

    fun insertBlockedWebData(blockedWebsiteEntity: BlockedWebsiteEntity) {
        mRepository.insertBlockedWebData(blockedWebsiteEntity)
    }

    fun insertAppData(appsDataEntity: AppsDataEntity) {
        mRepository.insertAppData(appsDataEntity)
    }

    // for managing questions
    fun checkAppByPackageName(packageName: String): Int {
        return mRepository.checkAppByPackageName(packageName)
    }

    fun checkCatByPackageName(packageName: String): Int {
        return mRepository.checkCatByPackageName(packageName)
    }

//    val getAllBlockedDataList: LiveData<List<BlockedAppsEntity>>
//        get() = mRepository.getAllBlockedDataList

    fun getAllBlockedDataList() = mRepository.getAllBlockedDataList()

    fun getAllBlockedCatsList() = mRepository.getAllBlockedCatsList()

    fun getAllBlockedWebList() = mRepository.getAllBlockedWebList()

    fun getAllAppsList() = mRepository.getAllAppsList()

    fun updateBlockedData(blockDataResponse: BlockedAppsEntity) {
        mRepository.updateBlockedData(blockDataResponse)
    }

    fun deleteAllBlockedApps() {
        mRepository.deleteAllBlockedApps()
    }

    fun deleteAllBlockedCats() {
        mRepository.deleteAllBlockedCats()
    }

    fun deleteAllBlockedWebsites() {
        mRepository.deleteAllBlockedWebsites()
    }
}
