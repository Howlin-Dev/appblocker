package com.serhii.appblocker.platform.accessibility

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.serhii.appblocker.R
import com.serhii.appblocker.core.domain.repository.BlockRepository
import com.serhii.appblocker.presentation.block.BlockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.jvm.java

@SuppressLint("AccessibilityPolicy")
class BlockAccessibilityService: AccessibilityService() {

    private val blockRepository: BlockRepository by inject()
    private var blockedPackages: Set<String> = emptySet()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isForegroundRunning = false

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceScope.launch {
            blockRepository.activeBlock.collect { lock ->
                blockedPackages = lock?.blockedPackages?.toSet() ?: emptySet()

                if (lock != null) {
                    startBlockingNotification()
                } else {
                    stopBlockingNotification()
                }
            }
        }

        createNotificationChannel()
    }

    private var lastBlockTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val currentPackage = event.packageName?.toString() ?: return

        Log.d("onAccessibilityEvent", "onAccessibilityEvent: $currentPackage")

        val now = System.currentTimeMillis()
        if (now - lastBlockTime < 1500) return

        if (blockedPackages.contains(currentPackage)) {
            Log.d("onAccessibilityEvent", "onAccessibilityEvent (contains): $currentPackage")
            lastBlockTime = now
//            goToHomeScreen()
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
        Log.d("launchBlockScreen", "launchBlockScreen LAUNCHING")
        startActivity(intent)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "Blocking Service",
                "Blocking Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when app blocking is active"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startBlockingNotification() {
        if (isForegroundRunning) return

        startForeground(
            1,
            buildNotification()
        )

        isForegroundRunning = true
    }

    private fun stopBlockingNotification() {
        if (!isForegroundRunning) return

        stopForeground(STOP_FOREGROUND_REMOVE)

        isForegroundRunning = false
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "Blocking Service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Blocking active")
            .setContentText("Apps are currently blocked")
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()
    }

    override fun onInterrupt() {
        // Required override, no-op
    }
}