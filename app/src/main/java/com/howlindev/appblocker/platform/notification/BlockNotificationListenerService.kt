package com.howlindev.appblocker.platform.notification

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.permissions.platform.PermissionNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BlockNotificationListenerService : NotificationListenerService() {

    private val blockRepository: BlockRepository by inject()
    private var blockedPackages: Set<String> = emptySet()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Service connected")

        serviceScope.launch {
            blockRepository.activeBlock.collect { lock ->
                blockedPackages = lock?.blockedPackages?.toSet() ?: emptySet()

                // If a block is active, we might want to clear existing notifications from blocked apps
                if (blockedPackages.isNotEmpty()) {
                    cancelNotificationsFromBlockedApps()
                }
            }
        }

        if (PermissionNavigator.shouldAutoReturn(this)) {
            // Bring back from settings when the permission enabled
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName = sbn?.packageName ?: return

        if (blockedPackages.contains(packageName)) {
            Log.d("NotificationListener", "Blocking notification from $packageName")
            cancelNotification(sbn.key)
        }
    }

    private fun cancelNotificationsFromBlockedApps() {
        try {
            activeNotifications?.forEach { sbn ->
                if (blockedPackages.contains(sbn.packageName)) {
                    cancelNotification(sbn.key)
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error cancelling notifications", e)
        }
    }
}
