package com.codecoy.balancelauncherapp.ui.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.callbacks.DateAndTimeChangeCallback
import com.codecoy.balancelauncherapp.callbacks.SwipeLeftRightCallback
import com.codecoy.balancelauncherapp.databinding.ActivityLockBinding
import com.codecoy.balancelauncherapp.receivers.DateTimeChangeReceiver
import com.codecoy.balancelauncherapp.swipegesture.SwipeListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LockActivity : AppCompatActivity(), SwipeLeftRightCallback, DateAndTimeChangeCallback {
    private val tag = "LockActivity"
    private lateinit var dateTimeChangeReceiver: DateTimeChangeReceiver
    private lateinit var swipeListener: SwipeListener

    private lateinit var myView: View

    private var originalY = 0f

    private lateinit var mBinding: ActivityLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLockBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        inIt()

        mBinding.root.isClickable = true
        myView = mBinding.root
        swipeListener = SwipeListener(this, mBinding.root)
    }

    private fun inIt() {
        mBinding.tvDate.text = getFormattedDate()
        mBinding.tvOtherDate.text = getCurrentDate()
        mBinding.tvTime.text = getCurrentTime()

        dragSwipeUp()

        mBinding.ivDialer.setOnClickListener {
            openDialer()
        }

        mBinding.ivCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun openDialer() {
        val intent = Intent(Intent.ACTION_DIAL)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivity(takePictureIntent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragSwipeUp() {
        mBinding.tvSwipe.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Store the original Y position when the touch event starts
                    originalY = mBinding.tvSwipe.y
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Animate the view upward when it's released
                    animateUpward()
                    true
                }

                else -> false
            }
        }
    }

    private fun animateUpward() {
        // Create an ObjectAnimator to animate the translationY property of the view
        val animator = ObjectAnimator.ofFloat(mBinding.tvSwipe, "translationY", 0f, -300f)
        animator.apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            // When the animation is finished, reset the view to its original position
            addListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mBinding.tvSwipe.visibility = View.GONE
                        try {
//                        val action =
//                            LockScreenFragmentDirections.actionLockScreenFragmentToLockPatternFragment()

                            val navOptions =
                                NavOptions.Builder()
                                    .setEnterAnim(R.anim.bottom_slide_up)
                                    .setExitAnim(R.anim.slide_out_to_bottom)
                                    .setPopEnterAnim(R.anim.bottom_slide_up)
                                    .setPopExitAnim(R.anim.slide_out_to_bottom)
                                    .build()

//                        findNavController().navigate(action, navOptions)

                            startActivity(Intent(this@LockActivity, LockPatternActivity::class.java))
                        } catch (e: Exception) {
                            mBinding.tvSwipe.visibility = View.VISIBLE
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        mBinding.tvSwipe.y = originalY
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                },
            )
        }
        animator.start()
    }

    private fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun getFormattedDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE d", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getCurrentTime(): String {
        val currentTime = Date()
        val format = SimpleDateFormat("h:mm", Locale.getDefault())
        return format.format(currentTime)
    }

    override fun onSwipeLeft() {
//        val action = LockScreenFragmentDirections.actionLockScreenFragmentToHomeFragment()
//        findNavController().navigate(action)
    }

    override fun onSwipeRight() {
//        val action = LockScreenFragmentDirections.actionLockScreenFragmentToHomeFragment()
//        findNavController().navigate(action)
    }

    override fun onSwipeUp() {
//        val action = LockScreenFragmentDirections.actionLockScreenFragmentToLockPatternFragment()
//
//        val navOptions = NavOptions.Builder()
//            .setEnterAnim(R.anim.bottom_slide_up)
//            .setExitAnim(R.anim.bottom_fade_out)
//            .build()
//
//        findNavController().navigate(action, navOptions)
        super.onSwipeUp()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        dateTimeChangeReceiver = DateTimeChangeReceiver(this)
        register()
        Log.i(tag, "life_cycle:: onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregister()
        Log.i(tag, "life_cycle:: onDestroy")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun register() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_DATE_CHANGED)
        filter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(dateTimeChangeReceiver, filter, RECEIVER_EXPORTED)
    }

    private fun unregister() {
        unregisterReceiver(dateTimeChangeReceiver)
    }

    override fun onTimeChanged() {
        mBinding.tvTime.text = getCurrentTime()
    }

    override fun onDateChanged() {
        mBinding.tvDate.text = getFormattedDate()
        mBinding.tvOtherDate.text = getCurrentDate()
    }
}
