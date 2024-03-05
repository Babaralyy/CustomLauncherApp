package com.codecoy.balancelauncherapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.R.drawable
import com.codecoy.balancelauncherapp.databinding.FragmentPaymentBinding
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Utils
import com.google.firebase.crashlytics.internal.model.ImmutableList

class PaymentFragment : Fragment() {
    private lateinit var activity: MainActivity

    private lateinit var billingClient: BillingClient
    private lateinit var subBillingClient: BillingClient

    private lateinit var mBinding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentPaymentBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
//        mBinding.btnFree.setOnClickListener {
//
//            try {
//
//                Utils.savePaymentIntoPref(activity, true)
//
//                val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
//                findNavController().navigate(action)
//            }catch (e: Exception){
//
//            }
//
//        }

        setUpSubscription()

        mBinding.tvSubscription.setOnClickListener {
            setUpSubscription()
        }

        mBinding.tvOneTime.setOnClickListener {
            setUpOneTime()
        }

        mBinding.ivBack.setOnClickListener {
            try {
                findNavController().popBackStack()
            } catch (e: Exception) {
            }
        }
    }

    private fun setUpSubscription() {
        mBinding.tvSubscription.setBackgroundResource(drawable.purchase_back)
        mBinding.tvSubscription.setTextColor(ContextCompat.getColor(activity, R.color.black))

        mBinding.tvOneTime.setTextColor(ContextCompat.getColor(activity, R.color.white))
        mBinding.tvOneTime.background = null

        mBinding.tvPayment.text = "Billed annually."
        mBinding.tvPayment.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.subscription_color,
            ),
        )

        mBinding.tvLife.text = "12 months access, 3 days free trial."
        mBinding.btnFree.text = "Try it for free"
        mBinding.tvTrial.visibility = View.VISIBLE

        initializeSubBillingClient()
    }

    private fun setUpOneTime() {
        mBinding.tvOneTime.setTextColor(ContextCompat.getColor(activity, R.color.black))
        mBinding.tvOneTime.setBackgroundResource(drawable.purchase_back)

        mBinding.tvSubscription.background = null
        mBinding.tvSubscription.setTextColor(ContextCompat.getColor(activity, R.color.white))

        mBinding.tvPayment.text = "One payment."
        mBinding.tvPayment.setTextColor(ContextCompat.getColor(activity, R.color.one_time_color))

        mBinding.tvLife.text = "Life-time access."
        mBinding.btnFree.text = "I want it!"
        mBinding.tvTrial.visibility = View.INVISIBLE

        initializeOneBillingClient()
    }

    private fun initializeSubBillingClient() {
        mBinding.progressBar.visibility = View.VISIBLE

        Log.i("TAG", "SubBillingClient: --> initializeBillingClient")

        subBillingClient =
            BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener { billingResult, purchases ->

                    Log.i("TAG", "SubBillingClient: --> ${billingResult.responseCode}")

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (purchase in purchases) {
                            Log.i("TAG", "SubBillingClient: --> init BillingResponseCode.OK")

                            verifySubPurchase(purchase)
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                        mBinding.progressBar.visibility = View.GONE

                        Log.i("TAG", "SubBillingClient: --> init BillingResponseCode.USER_CANCELED")
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                        Log.i("TAG", "SubBillingClient: --> init else")

                        try {
                            Utils.saveSubIntoPref(activity, true)

                            val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                            findNavController().navigate(action)
                        } catch (e: Exception) {
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
                        mBinding.progressBar.visibility = View.GONE
                    } else {
                        mBinding.progressBar.visibility = View.GONE
                    }
                }
                .build()

        establishSubConnection()
    }

    private fun initializeOneBillingClient() {
        mBinding.progressBar.visibility = View.VISIBLE

        Log.i("TAG", "BillingClient: --> initializeBillingClient")

        billingClient =
            BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener { billingResult, purchases ->

                    Log.i("TAG", "BillingClient: --> ${billingResult.responseCode}")

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (purchase in purchases) {
                            Log.i("TAG", "BillingClient: --> init BillingResponseCode.OK")

                            verifyOnePurchase(purchase)
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Log.i("TAG", "BillingClient: --> init BillingResponseCode.USER_CANCELED")
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                        Log.i("TAG", "BillingClient: --> init else")

                        try {
                            Utils.savePaymentIntoPref(activity, true)

                            val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                            findNavController().navigate(action)
                        } catch (e: Exception) {
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_UNAVAILABLE) {
                    } else {
                    }
                }
                .build()

        establishConnection()
    }

    private fun establishSubConnection() {
        mBinding.progressBar.visibility = View.VISIBLE

        subBillingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.i(
                        "TAG",
                        "SubBillingClient: --> establishConnection ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                    )

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        processSubPurchases()

                        mBinding.progressBar.visibility = View.GONE
                        Log.i(
                            "TAG",
                            "SubBillingClient: --> establishConnection if ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.i("TAG", "SubBillingClient: --> onBillingServiceDisconnected")

                    establishSubConnection()
                }
            },
        )
    }

    private fun establishConnection() {
        mBinding.progressBar.visibility = View.VISIBLE

        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.i(
                        "TAG",
                        "BillingClient: --> establishConnection ${billingResult.responseCode}  ${BillingClient.BillingResponseCode.OK}",
                    )

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        processOnePurchases()

                        mBinding.progressBar.visibility = View.GONE
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

    private fun processSubPurchases() {
        Log.i("TAG", "SubBillingClient: --> processPurchases")

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.from(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(Utils.productSubId)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build(),
                    ),
                )
                .build()

        subBillingClient.queryProductDetailsAsync(queryProductDetailsParams) { _,
                                                                               productDetailsList,
            ->
            Log.i("TAG", "SubBillingClient: --> ${productDetailsList.size}")

            val productDetails = productDetailsList[0]

            Log.i("TAG", "SubBillingClient: --> ${productDetails.productId}")

            if (productDetails.productId == Utils.productSubId) {
                val subDetails = productDetails.subscriptionOfferDetails

                Log.i("TAG", "SubBillingClient: --> productDetails $subDetails")

                val price =
                    subDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice

                Log.i("TAG", "SubBillingClient: --> price $price")

                mBinding.btnFree.setOnClickListener {
                    Log.i("TAG", "SubBillingClient: --> launchPurchaseFlow")

                    launchSubPurchaseFlow(productDetails)
                }

                activity.runOnUiThread {
                    mBinding.tvAmount.text = price
                }
            }
        }
    }

    private fun processOnePurchases() {
        Log.i("TAG", "BillingClient: --> processPurchases")

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.from(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(Utils.productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    ),
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { _,
                                                                            productDetailsList,
            ->
            Log.i("TAG", "BillingClient: --> ${productDetailsList.size}")

            val productDetails = productDetailsList[0]

            Log.i("TAG", "BillingClient: --> $productDetails")

            if (productDetails.productId == Utils.productId) {
                val subDetails = productDetails.oneTimePurchaseOfferDetails

                val price = subDetails?.formattedPrice
                Log.i("TAG", "BillingClient: --> price $price")

                mBinding.btnFree.setOnClickListener {
                    Log.i("TAG", "BillingClient: --> launchPurchaseFlow")

                    launchPurchaseFlow(productDetails)
                }

                activity.runOnUiThread {
                    mBinding.tvAmount.text = price
                }
            }
        }
    }

    private fun launchSubPurchaseFlow(productDetails: ProductDetails?) {
        Log.i("TAG", "SubBillingClient: --> launchedPurchaseFlow")

        val productDetailsParamsList =
            ImmutableList.from(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails!!)
                    .setOfferToken(productDetails.subscriptionOfferDetails?.get(0)?.offerToken.toString())
                    .build(),
            )

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
        subBillingClient.launchBillingFlow(requireActivity(), billingFlowParams)
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails?) {
        Log.i("TAG", "BillingClient: --> launchedPurchaseFlow $productDetails")

        val productDetailsParamsList =
            ImmutableList.from(
                productDetails?.let {
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(it)
                        .build()
                },
            )

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private fun verifySubPurchase(purchase: Purchase?) {
        Log.i("TAG", "SubBillingClient: --> tart time ${System.currentTimeMillis()}")

        Log.i("TAG", "SubBillingClient: --> verifySubPurchase")

        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase?.purchaseToken.toString())
                .build()

        subBillingClient.acknowledgePurchase(
            acknowledgePurchaseParams,
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                try {
                    Utils.saveSubIntoPref(activity, true)

                    val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                    findNavController().navigate(action)
                } catch (e: Exception) {
                }

                Log.i("TAG", "SubBillingClient: --> Acknowledged")
            } else {
                Log.i("TAG", "SubBillingClient: --> Already")
            }
        }

        Log.i("TAG", "Purchase Token: " + purchase?.purchaseToken)
        Log.i("TAG", "Purchase Time: " + purchase?.purchaseTime)
        Log.i("TAG", "Purchase OrderID: " + purchase?.orderId)
    }

    private fun verifyOnePurchase(purchase: Purchase?) {
        Log.i("TAG", "BillingClient: --> tart time ${System.currentTimeMillis()}")

        Log.i("TAG", "BillingClient: --> verifySubPurchase")

        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase?.purchaseToken.toString())
                .build()

        billingClient.acknowledgePurchase(
            acknowledgePurchaseParams,
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                try {
                    Utils.savePaymentIntoPref(activity, true)

                    val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                    findNavController().navigate(action)
                } catch (e: Exception) {
                }

                Log.i("TAG", "BillingClient: --> Acknowledged")
            } else {
                Log.i("TAG", "BillingClient: --> Already")
            }
        }

        Log.i("TAG", "Purchase Token: " + purchase?.purchaseToken)
        Log.i("TAG", "Purchase Time: " + purchase?.purchaseTime)
        Log.i("TAG", "Purchase OrderID: " + purchase?.orderId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
