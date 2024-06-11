package com.codecoy.balancelauncherapp.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.queryPurchasesAsync
import com.codecoy.balancelauncherapp.BuildConfig.IS_IN_APP_PURCHASE_FLOW_ENABLED
import com.codecoy.balancelauncherapp.callbacks.NetworkCallback
import com.codecoy.balancelauncherapp.databinding.FragmentFreedomBinding
import com.codecoy.balancelauncherapp.receivers.NetworkChangeReceiver
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Constant
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FreedomFragment : Fragment(), NetworkCallback {
    private lateinit var billingClient: BillingClient
    private lateinit var activity: MainActivity
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var alertDialog: AlertDialog

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                // Process the updated purchases here
                // Example: Handle new purchases or updates to existing purchases
            } else {
                // Handle an error scenario
            }
        }

    private lateinit var mBinding: FragmentFreedomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentFreedomBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        alertDialog =
            AlertDialog.Builder(activity)
                .setTitle("Internet")
                .setMessage("Connect to the internet and try again.")
                .setCancelable(false)
                .create()

        networkChangeReceiver = NetworkChangeReceiver(this)
        val networkFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(
                networkChangeReceiver,
                networkFilter,
                AppCompatActivity.RECEIVER_NOT_EXPORTED,
            )
        } else {
            activity.registerReceiver(networkChangeReceiver, networkFilter)
        }

        mBinding.btnLaunch.setOnClickListener {
            mBinding.progressBar.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                try {
      /*              val isPayed = Utils.fetchPaymentFromPref(activity)
                    val isSubscribed = Utils.fetchSubFromPref(activity)
                    val showInAppPurchaseFlow =
                        !isPayed && !isSubscribed && IS_IN_APP_PURCHASE_FLOW_ENABLED

                    val action = if (showInAppPurchaseFlow) {
                        FreedomFragmentDirections.actionFreedomFragmentToPaymentFragment()
                    } else {
                        FreedomFragmentDirections.actionFreedomFragmentToHomeFragment()
                    }
                    findNavController().navigate(action)*/

                    findNavController().navigate(FreedomFragmentDirections.actionFreedomFragmentToLoginFragment())


                    mBinding.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                    Log.i(TAG, "exception:: ${e.message}")
                }
            }, 600)
        }

        mBinding.ivBack.setOnClickListener {
            try {
                val action = FreedomFragmentDirections.actionFreedomFragmentToPasscodeFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.i(TAG, "inIt: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initializeBillingClient() {
        val dialog = Constant.getDialog(activity)
        dialog.show()

        Log.i("TAG", "BillingClient: --> initializeBillingClient")

        billingClient =
            BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build()

        establishConnection(dialog)
    }

    private fun establishConnection(dialog: Dialog) {
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.i(
                        "TAG",
                        "BillingClient: --> establishConnection ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                    )

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        fetchOneTimeProducts()
                        fetchSubProducts(dialog)

                        Log.i(
                            "TAG",
                            "BillingClient: --> establishConnection if ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.i("TAG", "BillingClient: --> onBillingServiceDisconnected")

                    establishConnection(dialog)
                }
            },
        )
    }

    private fun fetchOneTimeProducts() {
        coroutineScope.launch {
            val purchasesResult: PurchasesResult =
                billingClient.queryPurchasesAsync(SkuType.INAPP)

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

    private fun fetchSubProducts(dialog: Dialog) {
        coroutineScope.launch {
            val purchasesResult: PurchasesResult =
                billingClient.queryPurchasesAsync(SkuType.SUBS)

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

            dialog.dismiss()
        }
    }

    override fun onConnected() {
        initializeBillingClient()
        alertDialog.dismiss()
    }

    override fun onDisconnected() {
        alertDialog.show()
    }

    override fun onStop() {
        try {
            activity.unregisterReceiver(networkChangeReceiver)
        } catch (e: Exception) {
            Log.i(TAG, "onStop: ${e.message}")
        }

        super.onStop()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
