package com.codecoy.balancelauncherapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.queryPurchasesAsync
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.callbacks.AppLauncherCallback
import com.codecoy.balancelauncherapp.callbacks.ChangeBlockDataCallback
import com.codecoy.balancelauncherapp.common.Constants.Companion.APP_PACKAGE_NAME
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedApps
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedAppsResponse
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatBody
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatData
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedCatResponse
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedWebsiteData
import com.codecoy.balancelauncherapp.data.responsemodel.BlockedWebsiteResponse
import com.codecoy.balancelauncherapp.databinding.FragmentHomeBinding
import com.codecoy.balancelauncherapp.databinding.InstallAppLayBinding
import com.codecoy.balancelauncherapp.network.ApiCall
import com.codecoy.balancelauncherapp.receivers.BlockDataChangeReceiver
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.roomdb.LauncherViewModel
import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedAppsEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedWebsiteEntity
import com.codecoy.balancelauncherapp.roomdb.entities.BlocksCatsEntity
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.ui.adapters.InstalledAppsAdapter
import com.codecoy.balancelauncherapp.ui.fadeitems.OpacityScrollListener
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import com.codecoy.balancelauncherapp.utils.isNetworkConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class HomeFragment :
    Fragment(),
    AppLauncherCallback,
    ChangeBlockDataCallback {
    private lateinit var launcherViewModel: LauncherViewModel

    private lateinit var activity: MainActivity
    private lateinit var installedAppsAdapter: InstalledAppsAdapter

    private lateinit var appList: MutableList<AppsDataEntity>
    private lateinit var appListToSend: MutableList<String>

    private lateinit var distinctList: MutableList<AppsDataEntity>

    private lateinit var emptyList: MutableList<AppsDataEntity>

    private lateinit var blockedAppsEntityList: MutableList<BlockedAppsEntity>

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var blockDataChangeReceiver: BlockDataChangeReceiver

    private lateinit var billingClient: BillingClient

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                // Process the updated purchases here
                // Example: Handle new purchases or updates to existing purchases
            } else {
                // Handle an error scenario
            }
        }

    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater)

        inIt()
        return mBinding.root
    }

    private fun inIt() {
        launcherViewModel = ViewModelProvider(this)[LauncherViewModel::class.java]

        appList = arrayListOf()
        emptyList = arrayListOf()
        blockedAppsEntityList = arrayListOf()

        appListToSend = arrayListOf()
        distinctList = arrayListOf()

        installedAppsAdapter = InstalledAppsAdapter()
        blockDataChangeReceiver = BlockDataChangeReceiver(this)

        mBinding.rvApps.layoutManager = LinearLayoutManager(activity)
        mBinding.rvApps.setHasFixedSize(true)

        val opacityScrollListener = OpacityScrollListener(mBinding.rvApps)
        mBinding.rvApps.addOnScrollListener(opacityScrollListener)

        getAllInstalledApps()

        setHomeScreenWallpaper()

        mBinding.etSearch.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    filterAppList(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )
    }

    private fun showLauncherSelection() {
        val roleManager =
            activity.getSystemService(Context.ROLE_SERVICE)
                as RoleManager

        if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        ) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
            startForResult.launch(intent)
        }
    }

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                // Perhaps log the result here.
            }
        }

    private fun setHomeScreenWallpaper() {
        val wallpaperBitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper)

        val wallpaperManager = WallpaperManager.getInstance(activity)

        try {
            wallpaperManager.setBitmap(wallpaperBitmap, null, true, WallpaperManager.FLAG_SYSTEM)
        } catch (e: Exception) {
            Log.i(TAG, "setHomeScreenWallpaper:: ${e.message}")
        }
    }

    private fun filterAppList(app: String) {
        val filteredList: ArrayList<AppsDataEntity> = ArrayList()
        for (item in appList) {
            if (item.appName?.lowercase(Locale.ROOT)
                    ?.contains(app.lowercase(Locale.ROOT)) == true
            ) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Log.i(TAG, "filterAppList:: is Empty")
            installedAppsAdapter.filterList(emptyList as ArrayList<AppsDataEntity>)
        } else {
            installedAppsAdapter.filterList(filteredList)
            Log.i(TAG, "filterAppList:: is not Empty")
        }
    }

    private fun getAllInstalledApps() {
        launcherViewModel.getAllAppsList().observe(
            viewLifecycleOwner,
        ) {
            setUpRecyclerView(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecyclerView(installedAppData: MutableList<AppsDataEntity>) {
//        appListToSend.clear()
//
//        with(installedAppData.iterator()) {
//            forEach {
//                if (it.packageName != "com.codecoy.balancelauncherapp") {
//                    if (it.isSystemApp == false) {
//                        appListToSend.add(it.packageName.toString())
//                    }
//                }
//            }
//        }
//
//        Handler(Looper.getMainLooper()).postDelayed({
//
//            val blockedCatBody = BlockedCatBody(appListToSend as ArrayList<String>)
//            getAllBlockedCategories(blockedCatBody)
//
//        }, 500)

        launcherViewModel.getAllBlockedDataList().observe(
            activity,
        ) { appsEntities ->
            if (!appsEntities.isNullOrEmpty()) {
                with(appsEntities.iterator()) {
                    forEach {
                        LauncherRoomDatabase.getDatabase(activity).db_dao()
                            .deleteAppData(it.packageName.toString())
                        Log.i(TAG, "deleteAppData:: app ${it.packageName}")
                    }
                }
            }
        }

        launcherViewModel.getAllBlockedCatsList().observe(
            activity,
        ) { appsEntities ->
            if (!appsEntities.isNullOrEmpty()) {
                with(appsEntities.iterator()) {
                    forEach {
                        if (it.isblocked == true) {
                            LauncherRoomDatabase.getDatabase(activity).db_dao()
                                .deleteAppData(it.packageName.toString())
                            Log.i(TAG, "deleteAppData:: cat ${it.packageName}")
                        }
                    }
                }
            }
        }

        installedAppData.sortWith(
            compareBy { it.appName },
        )

        Log.i(TAG, "setUpRecyclerView:: if isEmpty")

        distinctList =
            installedAppData.distinctBy {
                it.packageName
            } as MutableList

        val index = findIndexBasedOnField(distinctList)
        if (index != -1) {
            distinctList.removeAt(index)
        }

        appList.clear()
        appList.addAll(distinctList)

        distinctList.sortByDescending { it.notCount }

        installedAppsAdapter = InstalledAppsAdapter(activity, distinctList, this)
        mBinding.rvApps.adapter = installedAppsAdapter

        installedAppsAdapter.notifyDataSetChanged()
    }

    private fun findIndexBasedOnField(yourList: MutableList<AppsDataEntity>): Int {
        return yourList.indexOfFirst { it.packageName == APP_PACKAGE_NAME } // Returns -1 if the item is not found
    }

    override fun launchApp(packageName: String) {
        // Launch the app

        try {
            val intent = activity.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                startActivity(intent)
            } else {
                Toast.makeText(activity, "App not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "App not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun manageApp(
        packageName: String,
        mBinding: InstallAppLayBinding,
    ) {
        showPopup(mBinding.tvApp, packageName)
    }

    private fun showPopup(
        v: View,
        packageName: String,
    ) {
        val popup = PopupMenu(requireContext(), v)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.iAppInfo -> {
                    openAppInfo(packageName)
                    true
                }

                R.id.iUninstall -> {
                    uninstallApp(packageName)
                    true
                }

                else -> {
                    false
                }
            }
        }
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.option_menu, popup.menu)
        popup.show()
    }

    private fun openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun uninstallApp(packageName: String) {
        val intent =
            Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
            }

        intent.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
//        BackgroundManager.getInstance().init(requireContext()).startService()

        if (activity.isNetworkConnected()) {
            getAllBlockedApps()
            getAllBlockedWebsites()
        } else {
            Toast.makeText(activity, "Connect to the internet", Toast.LENGTH_SHORT).show()
        }

        val intentFilter = IntentFilter("com.codecoy.balancelauncherapp.notification.MESSAGE")
        activity.registerReceiver(
            blockDataChangeReceiver,
            intentFilter,
            AppCompatActivity.RECEIVER_NOT_EXPORTED,
        )

        initializeBillingClient()

        showLauncherSelection()

        super.onResume()
    }

    private fun initializeBillingClient() {
        Log.i("TAG", "BillingClient: --> initializeBillingClient")

        billingClient =
            BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build()

        establishConnection()
    }

    private fun establishConnection() {
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.i(
                        "TAG",
                        "BillingClient: --> establishConnection ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                    )

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        fetchOneTimeProducts()
                        fetchSubProducts()

                        Log.i(
                            "TAG",
                            "BillingClient: --> establishConnection if ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.i("TAG", "BillingClient: --> onBillingServiceDisconnected")

                    establishConnection()
                }
            },
        )
    }

    private fun fetchOneTimeProducts() {
        coroutineScope.launch {
            val purchasesResult: PurchasesResult =
                billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP)

            if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchasesList = purchasesResult.purchasesList
                if (purchasesList.isNotEmpty()) {
                    for (purchase in purchasesList) {
                        Log.i(TAG, "fetchOneTimeProducts:: ${purchase.quantity} ")

                        val sku = purchase.skus
                        val purchaseToken = purchase.purchaseToken

                        if (purchase.quantity >= 1) {
                            Utils.savePaymentIntoPref(activity, true)
                        } else {
                            Utils.savePaymentIntoPref(activity, false)
                        }
                    }
                } else {
                    Log.i(TAG, "fetchOneTimeProducts:: empty ")
                    Utils.savePaymentIntoPref(activity, false)
                }
            }
        }
    }

    private fun fetchSubProducts() {
        coroutineScope.launch {
            val purchasesResult: PurchasesResult =
                billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS)

            if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchasesList = purchasesResult.purchasesList
                if (purchasesList.isNotEmpty()) {
                    for (purchase in purchasesList) {
                        Log.i(TAG, "fetchSubProducts:: ${purchase.quantity} ")

                        val sku = purchase.skus
                        val purchaseToken = purchase.purchaseToken

                        if (purchase.quantity >= 1) {
                            Utils.saveSubIntoPref(activity, true)
                        } else {
                            Utils.saveSubIntoPref(activity, false)
                        }
                    }
                } else {
                    Log.i(TAG, "fetchSubProducts:: empty ")
                    Utils.saveSubIntoPref(activity, false)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun getAllBlockedApps() {
        Log.i(TAG, "getAllBlockedApps::")

        val appsApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val appsCAll = appsApi.getAllBlockedApps()

        coroutineScope.launch {
            appsCAll.enqueue(
                object : Callback<BlockedAppsResponse> {
                    override fun onResponse(
                        call: Call<BlockedAppsResponse>,
                        response: Response<BlockedAppsResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val appsData = response.body()
                            if (appsData != null) {
                                val appsList = appsData.data?.apps

                                insertBlockedAppsToDb(appsList)
                            }
                        } else {
                            Log.i(TAG, "getAllBlockedApps:: onFailure else ${response.message()}")
                        }
                    }

                    override fun onFailure(
                        call: Call<BlockedAppsResponse>,
                        t: Throwable,
                    ) {
                        Log.i(TAG, "getAllBlockedApps:: onFailure ${t.message}")
                    }
                },
            )
        }
    }

    private fun insertBlockedAppsToDb(appsList: ArrayList<BlockedApps>?) {
        try {
            launcherViewModel.deleteAllBlockedApps()
        } catch (e: Exception) {
            Log.i(TAG, "insertBlockedAppsToDb:: ${e.message}")
        }

        if (!appsList.isNullOrEmpty()) {
            with(appsList.iterator()) {
                forEach {
                    launcherViewModel.insertBlockedData(
                        BlockedAppsEntity(
                            id = it.id.toString(),
                            packageName = it.packageName,
                            appName = it.title,
                            isblocked = it.isBlocked,
                        ),
                    )

                    LauncherRoomDatabase.getDatabase(activity).db_dao()
                        .deleteAppData(it.packageName.toString())
                }
            }
        }
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
                            Log.i(TAG, "getAllBlockedCategories:: onFailure else ${response.message()}")
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

    private fun getAllBlockedWebsites() {
        Log.i(TAG, "getAllBlockedWebsites::")

        val websitesApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        val websitesCall = websitesApi.getAllBlockedWebsites()

        coroutineScope.launch {
            websitesCall.enqueue(
                object : Callback<BlockedWebsiteResponse> {
                    override fun onResponse(
                        call: Call<BlockedWebsiteResponse>,
                        response: Response<BlockedWebsiteResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val websData = response.body()
                            if (websData != null) {
                                val websList = websData.data
                                insertBlockedWebsToDb(websList)
                            }
                        } else {
                            Log.i(TAG, "getAllBlockedWebsites:: onFailure else ${response.message()}")
                        }
                    }

                    override fun onFailure(
                        call: Call<BlockedWebsiteResponse>,
                        t: Throwable,
                    ) {
                        Log.i(TAG, "getAllBlockedWebsites:: onFailure ${t.message}")
                    }
                },
            )
        }
    }

    private fun insertBlockedWebsToDb(websList: ArrayList<BlockedWebsiteData>) {
        try {
            launcherViewModel.deleteAllBlockedWebsites()
        } catch (e: Exception) {
            Log.i(TAG, "insertBlockedAppsToDb:: ${e.message}")
        }

        if (websList.isNotEmpty()) {
            with(websList.iterator()) {
                forEach {
                    launcherViewModel.insertBlockedWebData(
                        BlockedWebsiteEntity(
                            id = it.id.toString(),
                            webDomain = it.domain,
                            isBlocked = it.isBlocked,
                        ),
                    )
                }
            }
        }
    }

    override fun onAppBlockUnblock() {
        if (activity.isNetworkConnected()) {
            getAllBlockedApps()
            activity.getAllInstalledApps()
        } else {
            Toast.makeText(activity, "Connect to the internet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCatBlockUnblock() {
//        activity.getAllInstalledApps()
//
//        if (appListToSend.isNotEmpty()) {
//            val blockedCatBody = BlockedCatBody(appListToSend as ArrayList<String>)
//            getAllBlockedCategories(blockedCatBody)
//        }
    }

    override fun onWebBlockUnblock() {
        if (activity.isNetworkConnected()) {
            getAllBlockedWebsites()
        } else {
            Toast.makeText(activity, "Connect to the internet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        coroutineScope.coroutineContext.cancelChildren()
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
