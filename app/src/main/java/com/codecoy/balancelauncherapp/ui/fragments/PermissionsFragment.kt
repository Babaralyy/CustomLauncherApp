package com.codecoy.balancelauncherapp.ui.fragments

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.FragmentPermissionsBinding
import com.codecoy.balancelauncherapp.databinding.PermissionDialogLayBinding
import com.codecoy.balancelauncherapp.services.CustomAccessibleService
import com.codecoy.balancelauncherapp.ui.activities.MainActivity
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import com.google.android.material.bottomsheet.BottomSheetDialog

private const val PACKAGE_PREFIX = "package:"

class PermissionsFragment : Fragment() {
    private lateinit var activity: MainActivity
    private var mediaController: MediaController? = null
    private lateinit var videoPath: String

    private lateinit var mBinding: FragmentPermissionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentPermissionsBinding.inflate(inflater)
        inIt()

        return mBinding.root
    }

    private fun inIt() {
        val passCode = Utils.fetchConfirmPasscode(activity)

        mBinding.ivBack.setOnClickListener {
            val navOptions =
                NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_slide_in_right)
                    .setExitAnim(R.anim.fragment_slide_out_left)
                    .setPopEnterAnim(R.anim.fragment_slide_in_right)
                    .setPopExitAnim(R.anim.fragment_slide_out_left)
                    .build()

            val action =
                PermissionsFragmentDirections.actionPermissionsFragmentToOnboardingOneFragment()
            findNavController().navigate(action, navOptions)
        }

        clickListeners()

        mBinding.btnContinue.setOnClickListener {
            if (isUsageStatsPermissionGranted() and
                Settings.canDrawOverlays(
                    context,
                ) and isAccessibilityServiceEnabled() and isNotificationAccessGranted()
            ) {
                if (passCode != null) {
                    val action =
                        PermissionsFragmentDirections.actionPermissionsFragmentToFreedomFragment()
                    findNavController().navigate(action)
                } else {
                    val action =
                        PermissionsFragmentDirections.actionPermissionsFragmentToPasscodeFragment()
                    findNavController().navigate(action)
                }
            } else {
                Toast.makeText(activity, "Please grant all permissions", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.ivOne.setOnClickListener {
            showDemoDialog(
                "Balance Phone needs access usage statistics for the in-app blocking pop-up to work.",
                "To enable usage access:",
                "1. Press the button below and find ‘Balance Phone’",
                "2. Activate the slider next to ‘Balance Phone’",
                "",
                1,
            )
        }
        mBinding.ivTwo.setOnClickListener {
            showDemoDialog(
                "Balance Phone needs permission to display content over other apps to allow the in-app blocking pop-up to work.",
                "To enable display over other apps:",
                "1. Press the button below and find ‘Balance Phone’",
                "2. Activate the slider next to ‘Balance Phone’",
                "",
                2,
            )
        }
        mBinding.ivThree.setOnClickListener {
            showDemoDialog(
                "Balance Phone needs access accessibility services for the in-app blocking pop-up to work.",
                "To enable accessibility:",
                "1. Press the button below",
                "2. Within downloaded apps, press ‘Balance Phone’",
                "3. Activate the slider next to ‘Use Balance phone’",
                3,
            )
        }
        mBinding.ivFour.setOnClickListener {
            showDemoDialog(
                "Balance Phone needs permission to access notifications to display notifications in the home page.",
                "To enable notifications access:",
                "1. Press the button below and find ‘Balance Phone’",
                "2. Activate the slider next to ‘Balance Phone’",
                "",
                4,
            )
        }
    }

    private fun clickListeners() {
        mBinding.cardView0.setOnClickListener {
            if (!isNotificationAccessGranted()) {
                requestNotificationAccess()
            }
        }

        mBinding.cardView1.setOnClickListener {
            if (!isUsageStatsPermissionGranted()) {
                requestUsageStatsPermission()
            }
        }

        mBinding.cardView2.setOnClickListener {
            if (Settings.canDrawOverlays(activity)) {
                // Permission is already granted, you can proceed with your functionality.
            } else {
                // Permission is not granted; request it from the user.
                requestOverlayPermission()
            }
        }

        mBinding.cardView3.setOnClickListener {
            if (!isAccessibilityServiceEnabled()) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            } else {
                // The service is already enabled, you can proceed with your logic
            }
        }
    }

    private fun hasAccessRestrictedPerm(): Boolean {
        return try {
            val pm: PackageManager = activity.packageManager
            val permission =
                pm.checkPermission("android.permission.MANAGE_APPOPS", activity.packageName)
            return permission == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    private fun checkWriteSettingsPermission() {
        if (!Settings.System.canWrite(activity)) {
            Toast.makeText(activity, "Permission not Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkBattery() {
        val result = isIgnoringBatteryOptimizations(requireContext())

        Log.i(TAG, "checkBattery: result:$result")
        if (result) {
            val name = resources.getString(R.string.app_name)
            Toast.makeText(
                requireContext(),
                "Battery optimization -> All apps -> $name -> Don't optimize",
                Toast.LENGTH_LONG,
            ).show()

            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val perm =
            context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = context.applicationContext.packageName
        return perm.isIgnoringBatteryOptimizations(name)
    }

    private fun showDemoDialog(
        mainHeading: String,
        heading: String,
        insOne: String,
        insTwo: String,
        insThree: String,
        i: Int,
    ) {
        val inflater = LayoutInflater.from(activity)
        val permissionBinding = PermissionDialogLayBinding.inflate(inflater)

        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(permissionBinding.root)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        permissionBinding.tvMainHeading.text = mainHeading
        permissionBinding.tvHeading.text = heading
        permissionBinding.tvInsOne.text = insOne
        permissionBinding.tvInsTwo.text = insTwo
        permissionBinding.tvInsThree.text = insThree

        if (i == 1)
            {
                permissionBinding.cardView1.visibility = View.VISIBLE
            }
        if (i == 2)
            {
                permissionBinding.cardView2.visibility = View.VISIBLE
            }
        if (i == 3)
            {
                permissionBinding.cardView3.visibility = View.VISIBLE
            }
        if (i == 4)
            {
                permissionBinding.cardView0.visibility = View.VISIBLE
            }

        permissionBinding.cardView0.setOnClickListener {
            if (!isNotificationAccessGranted()) {
                requestNotificationAccess()
            }
            bottomSheetDialog.dismiss()
        }

        permissionBinding.cardView1.setOnClickListener {
            if (!isUsageStatsPermissionGranted()) {
                requestUsageStatsPermission()
            }

            bottomSheetDialog.dismiss()
        }

        permissionBinding.cardView2.setOnClickListener {
            if (Settings.canDrawOverlays(activity)) {
                // Permission is already granted, you can proceed with your functionality.
            } else {
                // Permission is not granted; request it from the user.
                requestOverlayPermission()
            }

            bottomSheetDialog.dismiss()
        }

        permissionBinding.cardView3.setOnClickListener {
            if (!isAccessibilityServiceEnabled()) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            } else {
                // The service is already enabled, you can proceed with your logic
            }

            bottomSheetDialog.dismiss()
        }

        if (isNotificationAccessGranted()) {
            permissionBinding.ivNot.setImageResource(R.drawable.check_ring_duotone)
            permissionBinding.cardView0.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            permissionBinding.ivNot.setImageResource(R.drawable.arrow_forward)
            permissionBinding.cardView0.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (isUsageStatsPermissionGranted()) {
            permissionBinding.ivUsage.setImageResource(R.drawable.check_ring_duotone)
            permissionBinding.cardView1.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            permissionBinding.ivUsage.setImageResource(R.drawable.arrow_forward)
            permissionBinding.cardView1.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (Settings.canDrawOverlays(context)) {
            permissionBinding.ivTop.setImageResource(R.drawable.check_ring_duotone)
            permissionBinding.cardView2.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            permissionBinding.ivTop.setImageResource(R.drawable.arrow_forward)
            permissionBinding.cardView2.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (isAccessibilityServiceEnabled()) {
            permissionBinding.ivAccess.setImageResource(R.drawable.check_ring_duotone)
            permissionBinding.cardView3.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            permissionBinding.ivAccess.setImageResource(R.drawable.arrow_forward)
            permissionBinding.cardView3.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        bottomSheetDialog.show()
    }

    private fun playVideo(
        permissionBinding: PermissionDialogLayBinding,
        i: Int,
    ) {
        mediaController = MediaController(activity)

        when (i) {
            1 -> {
                videoPath =
                    "android.resource://" + activity.applicationContext.packageName + "/" + R.raw.usage // Replace `your_video` with the name of your video file in the `raw` folder
            }

            2 -> {
                videoPath =
                    "android.resource://" + activity.applicationContext.packageName + "/" + R.raw.over_apps // Replace `your_video` with the name of your video file in the `raw` folder
            }

            3 -> {
                videoPath =
                    "android.resource://" + activity.applicationContext.packageName + "/" + R.raw.accessibility // Replace `your_video` with the name of your video file in the `raw` folder
            }

            4 -> {
                videoPath =
                    "android.resource://" + activity.applicationContext.packageName + "/" + R.raw.notification // Replace `your_video` with the name of your video file in the `raw` folder
            }
        }

        val videoUri = Uri.parse(videoPath)

        // Set media controller
        mediaController?.setAnchorView(permissionBinding.videoView)
        permissionBinding.videoView.setMediaController(mediaController)

        // Set video URI
        permissionBinding.videoView.setVideoURI(videoUri)

        permissionBinding.videoView.setOnPreparedListener { mediaPlayer ->

            // Start the video playback
            mediaPlayer.isLooping = true // This sets the video to loop
            mediaPlayer.start()
        }
    }

    private fun isNotificationAccessGranted(): Boolean {
        val packageName = activity.packageName
        val flat =
            Settings.Secure.getString(activity.contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(packageName)
    }

    private fun requestNotificationAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    private fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = activity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                activity.packageName,
            )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent =
            Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS,
                Uri.parse(PACKAGE_PREFIX + activity.packageName),
            )
        startActivity(intent)
    }

    private fun requestOverlayPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse(PACKAGE_PREFIX + activity.packageName)
            }
        startActivity(intent)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityService = ComponentName(activity, CustomAccessibleService::class.java)
        val accessibilitySettings =
            Settings.Secure.getString(
                activity.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            )
        return accessibilitySettings?.contains(accessibilityService.flattenToString()) == true
    }

    override fun onResume() {
        super.onResume()

        if (isNotificationAccessGranted()) {
            mBinding.ivNot.setImageResource(R.drawable.check_ring_duotone)
            mBinding.cardView0.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            mBinding.ivNot.setImageResource(R.drawable.arrow_forward)
            mBinding.cardView0.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (isUsageStatsPermissionGranted()) {
            mBinding.ivUsage.setImageResource(R.drawable.check_ring_duotone)
            mBinding.cardView1.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            mBinding.ivUsage.setImageResource(R.drawable.arrow_forward)
            mBinding.cardView1.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (Settings.canDrawOverlays(context)) {
            mBinding.ivTop.setImageResource(R.drawable.check_ring_duotone)
            mBinding.cardView2.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            mBinding.ivTop.setImageResource(R.drawable.arrow_forward)
            mBinding.cardView2.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (isAccessibilityServiceEnabled()) {
            mBinding.ivAccess.setImageResource(R.drawable.check_ring_duotone)
            mBinding.cardView3.setCardBackgroundColor(resources.getColor(R.color.light_dark))
        } else {
            mBinding.ivAccess.setImageResource(R.drawable.arrow_forward)
            mBinding.cardView3.setCardBackgroundColor(resources.getColor(R.color.not_back_color))
        }

        if (isNotificationAccessGranted() and isUsageStatsPermissionGranted() and
            Settings.canDrawOverlays(
                context,
            ) and isAccessibilityServiceEnabled()
        ) {
            mBinding.tvNext.setTextColor(Color.WHITE)

            val tintColor = ContextCompat.getColor(activity, R.color.white)
            mBinding.ivNext.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }
}
