package com.serhii.appblocker.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.serhii.appblocker.core.domain.repository.BlockRepository
import com.serhii.appblocker.presentation.block.BlockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("AccessibilityPolicy")
class BlockAccessibilityService: AccessibilityService() {

    private val blockRepository: BlockRepository by inject()
    private var blockedPackages: Set<String> = emptySet()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceScope.launch {
            blockRepository.activeBlock.collect { lock ->
                blockedPackages = lock?.blockedPackages?.toSet() ?: emptySet()
            }
        }
    }

    private var lastBlockTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val currentPackage = event.packageName?.toString() ?: return

        val now = System.currentTimeMillis()
        if (now - lastBlockTime < 500) return

        if (blockedPackages.contains(currentPackage)) {
            lastBlockTime = now
            goToHomeScreen()
            launchBlockScreen()
        }
    }

    private fun goToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
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