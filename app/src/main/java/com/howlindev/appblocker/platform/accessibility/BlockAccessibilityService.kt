package com.howlindev.appblocker.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.permissions.platform.PermissionNavigator
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
                if (BrowserHelper.isBrowser(currentPackage)) {
                    handleWebsiteBlocking(event)
                }
            }
            else -> { }
        }
    }

    private fun handleAppBlocking(packageName: String) {
        val now = System.currentTimeMillis()
        if (now - lastBlockTime < 1500) return

        if (blockedPackages.contains(packageName)) {
            Log.d("onAccessibilityEvent", "Blocking app: $packageName")
            lastBlockTime = now
            lastBlockedUrl = null
            BlockNavigator.launchBlockScreen(this, packageName)
        }
    }

    private fun handleWebsiteBlocking(event: AccessibilityEvent) {
        if (blockedWebsites.isEmpty()) return

        val rootNode = rootInActiveWindow ?: return
        try {
            val url = UrlFinder.findUrlInNode(rootNode) ?: return

            val isBlocked = blockedWebsites.any { blockedUrl ->
                url.contains(blockedUrl, ignoreCase = true)
            }

            if (isBlocked) {
                val now = System.currentTimeMillis()
                val cooldown = if (url == lastBlockedUrl) 5000L else 1500L
                if (now - lastBlockTime < cooldown) return

                lastBlockTime = now
                lastBlockedUrl = url

                Log.d("handleWebsiteBlocking", "Blocking website: $url")
                val browserPackage = event.packageName?.toString() ?: ""

                serviceScope.launch {
                    BlockNavigator.navigateBrowserAway(this@BlockAccessibilityService, browserPackage)
                    kotlinx.coroutines.delay(300)
                    BlockNavigator.launchBlockScreen(this@BlockAccessibilityService, browserPackage, url)
                }
            } else if (lastBlockedUrl != null && !url.contains(lastBlockedUrl!!)) {
                lastBlockedUrl = null
            }
        } finally {
            rootNode.recycle()
        }
    }

    override fun onInterrupt() {
        // Required override, no-op
    }
}
