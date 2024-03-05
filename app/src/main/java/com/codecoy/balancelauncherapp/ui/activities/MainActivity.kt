package com.codecoy.balancelauncherapp.ui.activities

import android.app.Activity
import android.app.AppOpsManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.android.billingclient.BuildConfig

import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.callbacks.ChangeBlockDataCallback
import com.codecoy.balancelauncherapp.callbacks.ManageAppCallback
import com.codecoy.balancelauncherapp.common.Constants.Companion.APP_PACKAGE_NAME
import com.codecoy.balancelauncherapp.data.model.InstalledAppData
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatBody
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatData
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatResponse
import com.codecoy.balancelauncherapp.network.ApiCall
import com.codecoy.balancelauncherapp.receivers.AppAddRemoveReceiver
import com.codecoy.balancelauncherapp.receivers.BlockDataChangeReceiver
import com.codecoy.balancelauncherapp.receivers.MyDeviceAdminReceiver
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.roomdb.LauncherViewModel
import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlocksCatsEntity
import com.codecoy.balancelauncherapp.services.CustomAccessibleService
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import com.codecoy.balancelauncherapp.utils.isNetworkConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), ManageAppCallback, ChangeBlockDataCallback {
    private lateinit var launcherViewModel: LauncherViewModel

    private lateinit var installedAppListLive: MutableLiveData<MutableList<InstalledAppData>>
    private lateinit var installedAppList: MutableList<InstalledAppData>

    private lateinit var appListToSend: MutableList<String>
    private lateinit var blockDataChangeReceiver: BlockDataChangeReceiver

    private val doNotGoBack = false

    private val appListener = AppAddRemoveReceiver(this)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        inIt()
    }

    private fun inIt() {
        val started = Utils.fetchGetStartedFromPref(this)
        val confirmPasscode = Utils.fetchConfirmPasscode(this)

        val isPayed = Utils.fetchPaymentFromPref(this)
        val isSubscribed = Utils.fetchSubFromPref(this)
        val showInAppPurchaseFlow =
            !isPayed && !isSubscribed && BuildConfig.IS_IN_APP_PURCHASE_FLOW_ENABLED

        val isEmailAdded = Utils.fetchRecoveryEmail(this)
        val isOnBoard = Utils.fetchOnBoardFromPref(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)

        if (started && isOnBoard) {
            if (isUsageStatsPermissionGranted() and
                Settings.canDrawOverlays(
                    this,
                ) and isAccessibilityServiceEnabled() and isNotificationAccessGranted() && isEmailAdded != null
            ) {
                if (confirmPasscode == null) {
                    graph.setStartDestination(R.id.lockPatternFragment)
                } else {
                    if (showInAppPurchaseFlow) {
                        graph.setStartDestination(R.id.paymentFragment)
                    } else {
                        graph.setStartDestination(R.id.homeFragment)
                    }
                }
            } else {
                graph.setStartDestination(R.id.permissionsFragment)
            }
        } else {
            graph.setStartDestination(R.id.onboardingOneFragment)
        }

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)

        launcherViewModel = ViewModelProvider(this)[LauncherViewModel::class.java]

        installedAppListLive = MutableLiveData()

        installedAppList = arrayListOf()
        appListToSend = arrayListOf()

        blockDataChangeReceiver = BlockDataChangeReceiver(this)

        getAllInstalledApps()

        setBottomNavigationColor()

        try {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =
                resources.getColor(
                    R.color.black,
                    null,
                )
        } catch (e: Exception) {
            Log.i(TAG, "Exception:: ${e.message}")
        }

        setLockScreenWallpaper()
    }

    private fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(
            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
            ComponentName(this, MyDeviceAdminReceiver::class.java),
        )
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "onActivityResult:: RESULT OK")
            } else {
                Log.i(TAG, "onActivityResult:: RESULT NOT OK")
            }
        }

    private fun setLockScreenWallpaper() {
        val wallpaperBitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper)

        val wallpaperManager = WallpaperManager.getInstance(this)

        try {
            wallpaperManager.setBitmap(wallpaperBitmap, null, true, WallpaperManager.FLAG_LOCK)
        } catch (e: Exception) {
            Log.i(TAG, "setLockScreenWallpaper:: ${e.message}")
        }
    }

    private fun setBottomNavigationColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
    }

    fun getAllInstalledApps() {
        appListToSend.clear()

        val packageManager: PackageManager = packageManager
        installedAppList.clear()

        coroutineScope.launch {
            val installedApps = getInstalledApps()

            installedApps.filter { appInfo ->
                appInfo.loadIcon(packageManager) != null
            }.map { appInfo ->
                val appName = appInfo.loadLabel(packageManager).toString()
                val packageName = appInfo.activityInfo.packageName
                val isSystem = isSystemApp(packageName, packageManager)

                if (!isSystem) {
                    if (packageName != APP_PACKAGE_NAME) {
                        appListToSend.add(packageName.toString())
                    }
                }

                InstalledAppData(
                    appName,
                    packageName,
                    null,
                    null,
                    isSystem,
                ) to
                        AppsDataEntity(
                            id = packageName,
                            appName = appName,
                            packageName = packageName,
                            isSystemApp = isSystem,
                        )
            }.forEach { (installedAppData, appsDataEntity) ->
                installedAppList.add(installedAppData)
                launcherViewModel.insertAppData(appsDataEntity)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (isNetworkConnected()) {
                    val blockedCatBody = BlockedCatBody(appListToSend as ArrayList<String>)
                    getAllBlockedCategories(blockedCatBody)
                } else {
                    Toast.makeText(this@MainActivity, "Connect to the internet", Toast.LENGTH_SHORT)
                        .show()
                }
            }, 500)
        }

//        coroutineScope.launch {
//
//            val installedApps = getInstalledApps()
//
//            if (installedApps.isNotEmpty()) {
//                val appsWithIcons = installedApps.filter { appInfo ->
//                    appInfo.loadIcon(packageManager) != null
//                }
//
//                // Filter out apps with icons
//                for (appInfo in appsWithIcons) {
//                    val appName = appInfo.loadLabel(packageManager).toString()
//                    val packageName = appInfo.activityInfo.packageName
//
//                    if (isSystemApp(packageName, packageManager)) {
//
//                        installedAppList.add(
//                            InstalledAppData(
//                                appName,
//                                packageName,
//                                null,
//                                null,
//                                true
//                            )
//                        )
//                        launcherViewModel.insertAppData(
//                            AppsDataEntity(
//                                id = packageName,
//                                appName = appName,
//                                packageName = packageName,
//                                isSystemApp = true
//                            )
//                        )
//
//                    } else {
//                        installedAppList.add(
//                            InstalledAppData(
//                                appName,
//                                packageName,
//                                null,
//                                null,
//                                false
//                            )
//                        )
//                        launcherViewModel.insertAppData(
//                            AppsDataEntity(
//                                id = packageName,
//                                appName = appName,
//                                packageName = packageName,
//                                isSystemApp = false
//                            )
//                        )
//                    }
//
//                }
//
//            }
//        }
    }

    private fun getAllBlockedCategories(blockedCatBody: BlockedCatBody) {
        Log.i(TAG, "getAllBlockedCategories::: apps list ${blockedCatBody.apps}")

        val catsApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val catsCall = catsApi.getAllBlockedCategories(blockedCatBody)

        coroutineScope.launch {
            catsCall.enqueue(
                object : Callback<BlockedCatResponse> {
                    override fun onResponse(
                        call: Call<BlockedCatResponse>,
                        response: Response<BlockedCatResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val catsData = response.body()
                            if (catsData != null) {
                                val catsList = catsData.data
                                insertBlockedCatsToDb(catsList)
                            }
                        } else {
                            Log.i(
                                TAG,
                                "getAllBlockedCategories:: onFailure else ${response.message()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<BlockedCatResponse>,
                        t: Throwable,
                    ) {
                        Log.i(TAG, "getAllBlockedCategories:: onFailure ${t.message}")
                    }
                },
            )
        }
    }

    private fun insertBlockedCatsToDb(catsList: ArrayList<BlockedCatData>?) {
        try {
            launcherViewModel.deleteAllBlockedCats()
        } catch (e: Exception) {
            Log.i(TAG, "insertBlockedAppsToDb:: ${e.message}")
        }

        if (!catsList.isNullOrEmpty()) {
            for (item in catsList) {
                Log.i(
                    TAG,
                    "insertBlockedCatsToDb:::: packageName ${item.packageName} isblocked ${item.isBlocked}",
                )

                launcherViewModel.insertBlockedCatData(
                    BlocksCatsEntity(
                        packageName = item.packageName,
                        isblocked = item.isBlocked,
                    ),
                )
            }
        }
    }

    private fun isSystemApp(
        packageName: String,
        packageManager: PackageManager,
    ): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val appInfo: ApplicationInfo? = packageInfo.applicationInfo

            appInfo != null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun getInstalledApps(): List<ResolveInfo> =
        coroutineScope {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            return@coroutineScope packageManager.queryIntentActivities(
                intent,
                PackageManager.GET_ACTIVITIES,
            )
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        registerAppReceiver()

        val intentFilter = IntentFilter("com.codecoy.balancelauncherapp.notification.MESSAGE")
        registerReceiver(blockDataChangeReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun registerAppReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(appListener, intentFilter, RECEIVER_EXPORTED)
    }

    override fun onAppInstalled(packageName: String?) {
        getAllInstalledApps()
    }

    override fun onAppRemoved(packageName: String?) {
        LauncherRoomDatabase.getDatabase(this).db_dao().deleteAppData(packageName.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterAppReceiver()
    }

    private fun unRegisterAppReceiver() {
        unregisterReceiver(appListener)
        unregisterReceiver(blockDataChangeReceiver)
    }

    private fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName,
            )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityService = ComponentName(this, CustomAccessibleService::class.java)
        val accessibilitySettings =
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            )
        return accessibilitySettings?.contains(accessibilityService.flattenToString()) == true
    }

    private fun isNotificationAccessGranted(): Boolean {
        val packageName = packageName
        val flat =
            Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(packageName)
    }

    override fun onCatBlockUnblock() {
        getAllInstalledApps()
    }

    override fun onBackPressed() {
        if (doNotGoBack) {
            super.onBackPressed()
        }
    }
}
