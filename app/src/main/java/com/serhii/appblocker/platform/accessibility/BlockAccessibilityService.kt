package com.serhii.appblocker.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.serhii.appblocker.presentation.block.BlockActivity

@SuppressLint("AccessibilityPolicy")
class BlockAccessibilityService: AccessibilityService() {

    private val blockedPackage = "com.instagram.android"
    private val myPackage by lazy { packageName }

    private var lastBlockTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val currentPackage = event.packageName?.toString() ?: return

        // Ignore your own app to prevent infinite loop
        if (currentPackage == myPackage) return

        // Basic throttle (avoid spamming activity launches)
        val now = System.currentTimeMillis()
        if (now - lastBlockTime < 500) return

        if (currentPackage == blockedPackage) {
            lastBlockTime = now
            launchBlockScreen()
        }
    }

    private fun launchBlockScreen() {
        val intent = Intent(this, BlockActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Required override, no-op
    }
}