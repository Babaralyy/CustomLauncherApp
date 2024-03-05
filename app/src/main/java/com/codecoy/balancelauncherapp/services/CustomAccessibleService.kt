package com.codecoy.balancelauncherapp.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.TextView
import com.codecoy.balancelauncherapp.appblock.receivers.LockReceiver
import com.codecoy.balancelauncherapp.appblock.receivers.UninstallReceiver
import com.codecoy.balancelauncherapp.roomdb.LauncherRoomDatabase
import com.codecoy.balancelauncherapp.ui.activities.BlockWebActivity
import com.codecoy.balancelauncherapp.utils.Utils
import com.codecoy.balancelauncherapp.utils.Utils.TAG
import java.util.Locale

class CustomAccessibleService : AccessibilityService() {
    private var browserApp = ""
    private var browserUrl = ""
    private var blockedUrls: MutableList<String> = arrayListOf()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i(TAG, "native:: packageName ${event?.packageName}")
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val parentNodeInfo = event.source ?: return
            event.packageName.toString()

            val packageName: String = event.packageName.toString()

            if (packageName == "com.microsoft.bing") {
                // Get the URL text from the identified element from Microsoft Bing
                findURLBarNode(rootInActiveWindow)
            }

            if (packageName == "com.opera.browser") {
                // Get the URL text from the identified element from Opera
                findURLBarNode(rootInActiveWindow)
            }

            if (packageName == "com.android.chrome") {
                // Get the URL text from the identified element from Opera
                findURLBarNode(rootInActiveWindow)
            }

            if (event.packageName == "org.mozilla.firefox") {
                findURLBarNode(rootInActiveWindow)
            }

            var browserConfig: SupportedBrowserConfig? =
                null
            for (supportedConfig in getSupportedBrowsers()) {
                if (supportedConfig.packageName == packageName) {
                    browserConfig = supportedConfig
                }
            }
            if (browserConfig == null) {
                return
            }
            val capturedUrl: String = captureUrl(parentNodeInfo, browserConfig, event).toString()
            parentNodeInfo.recycle()

            if (packageName != browserApp) {
                if (Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                    browserUrl = capturedUrl
                    browserApp = packageName
                    if (browserUrl.isNotEmpty()) {
                        val blockWebsites =
                            LauncherRoomDatabase.getDatabase(this).db_dao().getAllBlockedWebList()

                        Log.i(TAG, "onAccessibilityEvent:: if $blockWebsites")

                        if (blockWebsites.isNotEmpty()) {
                            blockedUrls.clear()
                            with(blockWebsites.iterator()) {
                                forEach {
                                    blockedUrls.add(it.webDomain.toString())
                                }
                            }
                        } else {
                            blockedUrls.clear()
                        }
                    }
                }
            } else {
                if (capturedUrl != browserUrl) {
                    if (Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                        browserUrl = capturedUrl
                        if (browserUrl.isNotEmpty()) {
                            val blockWebsites =
                                LauncherRoomDatabase.getDatabase(this).db_dao()
                                    .getAllBlockedWebList()

                            Log.i(TAG, "onAccessibilityEvent:: else if $blockWebsites")

                            if (blockWebsites.isNotEmpty()) {
                                blockedUrls.clear()
                                with(blockWebsites.iterator()) {
                                    forEach {
                                        blockedUrls.add(it.webDomain.toString())
                                    }
                                }
                            } else {
                                blockedUrls.clear()
                            }
                        }
                    }
                }
            }
        }

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()

            val intent = Intent(this, LockReceiver::class.java)
            intent.putExtra("appOnTop", packageName)
            sendBroadcast(intent)
        }

        // Check if the event is of type TYPE_WINDOW_CONTENT_CHANGED or TYPE_WINDOW_STATE_CHANGED
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        ) {
            // Get the root node of the current window
            val rootNode = rootInActiveWindow ?: return
            event.source ?: return

            Log.i(TAG, "readTextFromNode:: packageName ${event.packageName}")

            if (event.packageName == "com.android.chrome") {
                val viewId = "com.android.chrome:id/url_bar"
                // Look for elements that might contain the URL, like address bar or title
                val nodeInfoList = rootNode.findAccessibilityNodeInfosByViewId(viewId)

                if (nodeInfoList.isEmpty()) return

                val addressBarNodeInfo = nodeInfoList.first()
                val addressBarText = addressBarNodeInfo.text?.toString() ?: return

                val isHintText = addressBarText.contains(" ") // "Search or type URL"
                val isChromeAutoFillingTheURL = addressBarNodeInfo.textSelectionStart > 0

                if (isHintText || isChromeAutoFillingTheURL) return

                Log.i(TAG, "addressBarText:: $addressBarText")

                putMyUrl(addressBarText, "com.android.chrome")
            }

            if (event.packageName == "com.google.android.packageinstaller") {
                if (Utils.isReadyToUninstall) {
                    val passCode = Utils.fetchConfirmPasscode(this)
                    if (passCode != null) {
                        readTextFromNode(rootNode)
                    }
                }
            }
        }
    }

    private fun findURLBarNode(rootNodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (rootNodeInfo == null) return null

        val nodeQueue = ArrayDeque<AccessibilityNodeInfo>()
        nodeQueue.add(rootNodeInfo)

        while (!nodeQueue.isEmpty()) {
            val currentNode = nodeQueue.removeFirst()

            // Check if the node represents the URL bar by its attributes
            if (isURLBar(currentNode)) {
                return currentNode
            }

            val childCount = currentNode.childCount
            for (i in 0 until childCount) {
                val childNode = currentNode.getChild(i)
                if (childNode != null) {
                    nodeQueue.add(childNode)
                }
            }
        }

        return null
    }

    private fun isURLBar(node: AccessibilityNodeInfo): Boolean {
        // This could involve checking attributes like resource ID, class name, text, or other properties
        // Example: Check for a known URL bar resource ID or text content

        val resourceId = node.viewIdResourceName
        val text = node.text?.toString()

        if (text?.let { Patterns.WEB_URL.matcher(it).matches() } == true) {
            if (resourceId == "com.android.chrome:id/url_bar") {
                Log.i(TAG, "isURLBar:: url $text id $resourceId")

                val addressBarText = node.text?.toString() ?: return false

                val isHintText = addressBarText.contains(" ") // "Search or type URL"
                val isChromeAutoFillingTheURL = node.textSelectionStart > 0

                if (isHintText || isChromeAutoFillingTheURL) return false

                val finalUrl = text.toString().substringBefore('/')
                putMyUrl(finalUrl, "com.android.chrome")
            }

            if (resourceId == "com.microsoft.bing:id/iab_address_bar_text_view") {
                Log.i(TAG, "isURLBar:: url $text id $resourceId")

                if (text.contains("https://www.")) {
                    // Remove "https://www." from the URL
                    val modifiedUrl = text.replace("https://www.", "")

                    // Remove trailing "/"
                    val finalUrl = modifiedUrl.substringBefore('/')

                    Log.i(TAG, "bingUrl:: url $finalUrl")

                    putMyUrl(finalUrl, "com.microsoft.bing")
                } else {
                    // Remove "https://www." from the URL
                    val modifiedUrl = text.replace("https://", "")

                    // Remove trailing "/"
                    val finalUrl = modifiedUrl.substringBefore('/')

                    Log.i(TAG, "bingUrl:: url $finalUrl")

                    putMyUrl(finalUrl, "com.microsoft.bing")
                }
            }

            if (resourceId == "com.opera.browser:id/url_field") {
                Log.i(TAG, "isURLBar:: url $text id $resourceId")

                val stringWithoutAfterSlash = text.substringBefore("/")

                Log.i(TAG, "operaUrl:: url $stringWithoutAfterSlash")

                putMyUrl(stringWithoutAfterSlash, "com.opera.browser")
            }

            if (resourceId == "com.sec.android.app.sbrowser:id/location_bar_edit_text") {
                Log.i(TAG, "isURLBar:: url $text id $resourceId")

                // Remove trailing "/"

                if (text.contains("www.")) {
                    val modifiedUrl = text.substring(5)

                    Log.i(TAG, "operaUrl:: url if $modifiedUrl")

                    putMyUrl(modifiedUrl, "com.sec.android.app.sbrowser")
                } else {
                    Log.i(TAG, "operaUrl:: url else  $text")
                    val mString = "www.$text"
                    val modifiedUrl = mString.substring(5)
                    Log.i(TAG, "operaUrl:: url else after $modifiedUrl")
                    putMyUrl(modifiedUrl, "com.sec.android.app.sbrowser")
                }
            }

            if (resourceId == "org.mozilla.firefox:id/mozac_browser_toolbar_url_view") {
                Log.i(TAG, "isURLBar:: url $text id $resourceId")

                val finalUrl = text.toString().substringBefore('/')

                Log.i(TAG, "bingUrl:: url $finalUrl")

                putMyUrl(finalUrl, "org.mozilla.firefox")
            }
        }

        return false
    }

    private fun readTextFromNode(node: AccessibilityNodeInfo) {
        // Check if the node is a text node and fetch its text content
        if (node.className == EditText::class.java.name || node.className == TextView::class.java.name) {
            val nodeText = node.text?.toString()

            Log.i(TAG, "readTextFromNode:: text $nodeText")

            if (nodeText.toString().contains("Balance")) {
                val intent = Intent(this, UninstallReceiver::class.java)
                sendBroadcast(intent)
            }
        }

        // Recursively traverse child nodes
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode != null) {
                readTextFromNode(childNode)
            }
        }
    }

    private fun putMyUrl(
        url: String?,
        browser: String,
    ) {
        Utils.browserName = browser

        Log.i(TAG, "findUrlBar:: putMyUrl")

        Log.i(TAG, "findUrlBar:: before url $url")

        val isWebSiteBlock =
            LauncherRoomDatabase.getDatabase(this).db_dao().getAllBlockedWebListString()

        Log.i(TAG, "findUrlBar:: isWebSiteBlock $isWebSiteBlock url ${url is String}")

        if (isWebSiteBlock.contains(url?.lowercase(Locale.ROOT))) {
            Log.i(TAG, "findUrlBar:: website is blocked")

            val i = Intent(applicationContext, BlockWebActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
        } else {
            Log.i(TAG, "findUrlBar:: website is not blocked $url")
        }
    }

    private class SupportedBrowserConfig(var packageName: String, var addressBarId: String)

    private fun getSupportedBrowsers(): List<SupportedBrowserConfig> {
        val browsers: MutableList<SupportedBrowserConfig> =
            ArrayList()
        browsers.add(
            SupportedBrowserConfig(
                "com.android.chrome",
                "com.android.chrome:id/url_bar",
            ),
        )
        browsers.add(
            SupportedBrowserConfig(
                "org.mozilla.firefox",
                "org.mozilla.firefox:id/mozac_browser_toolbar_url_view",
            ),
        )

        browsers.add(
            SupportedBrowserConfig(
                "com.opera.browser",
                "com.opera.browser:id/url_field",
            ),
        )
        browsers.add(
            SupportedBrowserConfig(
                "com.opera.mini.native",
                "com.opera.mini.native:id/url_field",
            ),
        )

        browsers.add(
            SupportedBrowserConfig(
                "com.sec.android.app.sbrowser",
                "com.sec.android.app.sbrowser:id/location_bar_edit_text",
            ),
        )

        browsers.add(
            SupportedBrowserConfig(
                "com.microsoft.bing",
                "com.microsoft.bing:id/iab_address_bar_text_view",
            ),
        )

        return browsers
    }

    private fun captureUrl(
        info: AccessibilityNodeInfo,
        config: SupportedBrowserConfig,
        event: AccessibilityEvent,
    ): String? {
        val nodes = info.findAccessibilityNodeInfosByViewId(config.addressBarId)
        if (nodes == null || nodes.size <= 0) {
            return null
        }
        val addressBarNodeInfo = nodes[0]
        var url: String? = null
        if (addressBarNodeInfo.text != null) {
            url = addressBarNodeInfo.text.toString()
        }
        addressBarNodeInfo.refresh()

        val source = event.source

        if (source != null) {
            val parentNode = source.parent
            if (parentNode != null) {
                val isPressed = parentNode.isImportantForAccessibility
                if (isPressed) {
                    Log.i(TAG, "captureUrl::  true $url")

                    return url
                }
            } else {
                Log.i(TAG, "captureUrl::  else $url")
                return url
            }
        }

        return url
    }

    override fun onInterrupt() {
        Log.i(TAG, "onInterrupt:: onInterrupt")
    }

    override fun onServiceConnected() {
        Log.i(TAG, "onInterrupt:: onServiceConnected")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        Log.i(TAG, "captureUrl::  Url onKeyEvent")

        if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            Log.i(TAG, "captureUrl::  Url onKeyEvent")
        }
        return super.onKeyEvent(event)
    }
}
