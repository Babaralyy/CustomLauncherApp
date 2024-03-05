package com.codecoy.balancelauncherapp.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codecoy.balancelauncherapp.databinding.ActivityBlockWebBinding
import com.codecoy.balancelauncherapp.utils.Utils

class BlockWebActivity : AppCompatActivity() {
    private var url = ""
    private var packages = ""
    private val isBack = false

    private lateinit var mBinding: ActivityBlockWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBlockWebBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        inIt()
    }

    private fun inIt() {
        url = intent.getStringExtra("URL").toString()
        packages = intent.getStringExtra("PACKAGE").toString()

        mBinding.btnClose.setOnClickListener {
//            val urlString = "https://www.google.com"
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.setPackage(Utils.browserName)
//            try {
//                startActivity(intent)
//            } catch (ex: ActivityNotFoundException) {
//                intent.setPackage(null)
//                startActivity(intent)
//            }

            val url = "https://www.google.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            if (Utils.browserName.isNotEmpty()) {
                intent.`package` = Utils.browserName // Package name of Chrome browser
            }

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Chrome is not installed
                // Handle the case where Chrome is not available on the device
            }

//            val newUrl = "http://www.google.com" // Your URL here
//
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//            val chooser = Intent.createChooser(intent, "Open URL")
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(chooser)
//            }

//            finish()
        }
    }

    override fun onBackPressed() {
        if (isBack) {
            super.onBackPressed()
        }
    }
}
