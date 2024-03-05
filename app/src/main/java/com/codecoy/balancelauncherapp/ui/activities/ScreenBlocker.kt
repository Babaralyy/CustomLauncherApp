package com.codecoy.balancelauncherapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.codecoy.balancelauncherapp.R
import com.codecoy.balancelauncherapp.databinding.ActivityScreenBlockerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ScreenBlocker : AppCompatActivity() {
    private lateinit var mBinding: ActivityScreenBlockerBinding
    private val isBack = false
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_screen_blocker)
        initIconApp()

        mBinding.btnClose.setOnClickListener(
            View.OnClickListener {
//            GlobalScope.launch(Dispatchers.Main) {
//                val startMain = Intent(Intent.ACTION_MAIN)
//                startMain.addCategory(Intent.CATEGORY_HOME)
//                startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//                // Start the activity on the UI thread
//                startActivity(startMain)
//
//            }

                coroutineScope.launch {
                    runOnUiThread {
                        mBinding.progressBar.visibility = View.VISIBLE
                    }

                    launchHomeApp()
                }

//            val startMain = Intent(Intent.ACTION_MAIN)
//            startMain.addCategory(Intent.CATEGORY_HOME)
//            startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(startMain)
//            finish()
            },
        )
    }

    private suspend fun launchHomeApp() =
        coroutineScope {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }

    private fun initIconApp() {
    }

    override fun onBackPressed() {
        if (isBack)
            {
                super.onBackPressed()
            }

//        val startMain = Intent(Intent.ACTION_MAIN)
//        startMain.addCategory(Intent.CATEGORY_HOME)
//        startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(startMain)
//        finish()

//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
    }
}
