package com.howlindev.appblocker.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.permissions.platform.PermissionNavigator
import com.howlindev.appblocker.presentation.block.BlockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("AccessibilityPolicy")
class BlockAccessibilityService : AccessibilityService() {

    private val blockRepository: BlockRepository by inject()
    private var blockedPackages: Set<String> = emptySet()
    private var blockedWebsites: List<String> = emptyList()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.flags = info.flags or
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        serviceInfo = info

        serviceScope.launch {
            blockRepository.activeBlock.collect { lock ->
                blockedPackages = lock?.blockedPackages?.toSet() ?: emptySet()
                blockedWebsites = lock?.blockedWebsites ?: emptyList()
            }
        }

        if (PermissionNavigator.shouldAutoReturn(this)) {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
    }

    private var lastBlockTime = 0L
    private var lastBlockedUrl: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val currentPackage = event.packageName?.toString() ?: return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleAppBlocking(currentPackage)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (isBrowser(currentPackage)) {
                    handleWebsiteBlocking(event)
                }
            }
        }
    }

    private fun handleAppBlocking(packageName: String) {
        val now = System.currentTimeMillis()
        if (now - lastBlockTime < 1500) return

        if (blockedPackages.contains(packageName)) {
            Log.d("onAccessibilityEvent", "onAccessibilityEvent (contains): $packageName")
            lastBlockTime = now
            lastBlockedUrl = null // Reset URL tracking when app is blocked
            launchBlockScreen(packageName)
        }
    }

    private fun isBrowser(packageName: String): Boolean {
        val browsers = listOf(
            "com.android.chrome",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.opera.browser",
            "com.duckduckgo.mobile.android",
            "com.brave.browser",
            "com.sec.android.app.sbrowser"
        )
        return browsers.contains(packageName)
    }

    private fun handleWebsiteBlocking(event: AccessibilityEvent) {
        if (blockedWebsites.isEmpty()) return

        val rootNode = rootInActiveWindow ?: return
        try {
            val url = findUrlInNode(rootNode) ?: return

            val isBlocked = blockedWebsites.any { blockedUrl ->
                url.contains(blockedUrl, ignoreCase = true)
            }

            if (isBlocked) {
                val now = System.currentTimeMillis()
                
                // Use a longer cooldown if we are still seeing the same blocked URL
                // This gives the redirection intent time to be processed by the browser.
                val cooldown = if (url == lastBlockedUrl) 5000L else 1500L
                if (now - lastBlockTime < cooldown) return
                
                lastBlockTime = now
                lastBlockedUrl = url

                Log.d("handleWebsiteBlocking", "Blocking website: $url")
                val browserPackage = event.packageName?.toString() ?: ""
                
                // Use a coroutine to sequence the redirection and the block screen.
                // Redirection is triggered first, then a short delay ensures the browser
                // receives the intent before the BlockActivity takes over focus.
                serviceScope.launch {
                    navigateBrowserAway(browserPackage)
                    kotlinx.coroutines.delay(300)
                    launchBlockScreen(browserPackage)
                }
            } else if (lastBlockedUrl != null && !url.contains(lastBlockedUrl!!)) {
                // If we see a different, non-blocked URL, reset the tracker
                lastBlockedUrl = null
            }
        } finally {
            rootNode.recycle()
        }
    }

    private fun navigateBrowserAway(browserPackage: String) {
        if (browserPackage.isBlank()) return
        try {
            // Force the browser to navigate to a safe page and clear the task stack
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                setPackage(browserPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or 
                         Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                         Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("BlockAccessibility", "Failed to navigate browser away", e)
        }
    }

    private fun findUrlInNode(node: AccessibilityNodeInfo): String? {
        val browserAddressBarIds = listOf(
            "com.android.chrome:id/url_bar",
            "org.mozilla.firefox:id/url_bar_title",
            "com.microsoft.emmx:id/url_bar",
            "com.brave.browser:id/url_bar",
            "com.sec.android.app.sbrowser:id/location_bar_edit_text",
            "com.duckduckgo.mobile.android:id/omnibar_text",
            "com.opera.browser:id/url_field"
        )

        var knownAddressBarFound = false
        for (id in browserAddressBarIds) {
            val nodes = node.findAccessibilityNodeInfosByViewId(id)
            if (nodes.isNotEmpty()) {
                knownAddressBarFound = true
                val urlNode = nodes[0]
                val isFocused = urlNode.isFocused
                val text = urlNode.text?.toString()

                // Recycle all found nodes
                nodes.forEach { it.recycle() }

                if (isFocused) {
                    // User is actively typing in the address bar, don't block based on this text
                    return null
                }

                if (!text.isNullOrBlank() && (text.contains(".") || text.contains("://"))) {
                    return text
                }
            }
        }

        // Only fallback to heuristics if no known address bar was found in this window
        if (knownAddressBarFound) return null

        return findUrlByHeuristics(node)
    }

    private fun findUrlByHeuristics(node: AccessibilityNodeInfo, isInsideList: Boolean = false): String? {
        val className = node.className?.toString() ?: ""
        val currentIsList = isInsideList ||
            className.contains("ListView") ||
            className.contains("RecyclerView") ||
            className.contains("AbsListView") ||
            className.contains("List")

        // If an EditText is focused, it's likely the user is typing
        if (node.className == "android.widget.EditText" && node.isFocused) {
            return null
        }

        // Avoid matching URLs inside lists to prevent blocking based on autocomplete suggestions
        if (!currentIsList && (node.className == "android.widget.EditText" || node.className == "android.widget.TextView")) {
            val text = node.text?.toString()
            if (text != null && text.length > 3 && (text.contains(".") || text.contains("://"))) {
                return text
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findUrlByHeuristics(child, currentIsList)
            child.recycle()
            if (result != null) return result
        }
        return null
    }

    private fun launchBlockScreen(packageName: String) {
        val intent = Intent(this, BlockActivity::class.java).apply {
            putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP,
            )
        }
        Log.d("launchBlockScreen", "launchBlockScreen LAUNCHING for $packageName")
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Required override, no-op
    }
}
