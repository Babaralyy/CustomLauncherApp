package com.codecoy.balancelauncherapp.appblock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codecoy.balancelauncherapp.appblock.BlockWebDataModel
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.ui.activities.BlockWebActivity
import com.codecoy.balancelauncherapp.utils.Utils

class BlockWebReceiver : BroadcastReceiver() {
    private var browserApp = ""
    private var browserUrl = ""
    private var blockedUrls: MutableList<String> = arrayListOf()

    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        if (intent?.extras != null) {
            val extras = intent.extras
            val blockWebDataModel = extras?.getParcelable("urlOnTop") as? BlockWebDataModel

            if (blockWebDataModel != null)
                {
                    val urlOntOp = blockWebDataModel.url.toString()
                    val packageName = blockWebDataModel.packageName.toString()

                    val isWebSiteBlock =
                        LauncherRoomDatabase.getDatabase(context).db_dao().checkWebByDomain(urlOntOp)

                    if (isWebSiteBlock == 1)
                        {
                            Log.i(Utils.TAG, "findUrlBar:: website is blocked")

                            val i = Intent(context, BlockWebActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                            try {
                                context.startActivity(i)
                            } catch (e: Exception) {
                                Log.i(Utils.TAG, "findUrlBar:: Exception ${e.message}")
                            }
                        } else {
                        Log.i(Utils.TAG, "findUrlBar:: website is not blocked $urlOntOp")
                    }

//                if (Patterns.WEB_URL.matcher(urlOntOp).matches()) {
//                    browserUrl = urlOntOp
//                    browserApp = packageName
//                    var url: String? = ""
//                    if (browserUrl.isNotEmpty()) {
//                        if (browserUrl.contains("/")) {
//                            url = browserUrl.substring(0, browserUrl.indexOf("/"))
//                        } else {
//                            if (!browserUrl.contains(" ")) {
//                                url = browserUrl
//                            }
//                        }

//                        val blockWebsites =
//                            LauncherRoomDatabase.getDatabase(context).db_dao().getAllBlockedWebList()
//                        Log.i(Utils.TAG, "onAccessibilityEvent:: if $blockWebsites")
//
//                        if (blockWebsites.isNotEmpty()) {
//
//                            Log.i(Utils.TAG, "getUrlOnTop:: blockWebsites")
//
//                            blockedUrls.clear()
//                            with(blockWebsites.iterator()) {
//                                forEach {
//                                    blockedUrls.add(it.webDomain.toString())
//                                }
//                            }
//                        } else {
//                            blockedUrls.clear()
//                        }
//
//                        if (url != null) {
//                            if (blockedUrls.contains(url)) {
//
//                                Log.i(Utils.TAG, "getUrlOnTop:: contains $urlOntOp")
//
//                                val lockIntent = Intent(context, BlockWebActivity::class.java)
//                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                lockIntent.putExtra("URL", url)
//                                lockIntent.putExtra("PACKAGE", browserApp)
//                                context.startActivity(lockIntent)
//
//                            }
//                        }
//                    }
//                }
                    Log.i(Utils.TAG, "getUrlOnTop:: $urlOntOp")
                }
        }
    }
}
