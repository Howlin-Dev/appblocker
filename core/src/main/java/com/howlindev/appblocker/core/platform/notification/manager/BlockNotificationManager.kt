package com.howlindev.appblocker.core.platform.notification.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.howlindev.appblocker.core.R
import com.howlindev.appblocker.core.domain.model.ActiveBlock
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.core.util.millisToTimerString

class BlockNotificationManager(
    private val context: Context,
    private val profilesRepository: ProfilesRepository,
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "active_profile_channel"
        private const val NOTIFICATION_ID = 1001
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    suspend fun updateNotification(activeBlock: ActiveBlock?, remainingMillis: Long) {
        if (activeBlock == null) {
            cancelNotification()
            return
        }

        val profile = activeBlock.profileId?.let { profilesRepository.getById(it) }
        if (profile == null) {
            cancelNotification()
            return
        }
        val title = context.getString(R.string.notification_active_profile_title, profile.name)
        val appsCount = profile.appPackages.size
        val body = context.getString(R.string.notification_active_profile_body, appsCount)

        val timerText = if (activeBlock.hasTimer) {
            context.getString(R.string.notification_active_profile_timer, remainingMillis.millisToTimerString(context))
        } else {
            ""
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bloq)
            .setContentTitle(title)
            .setContentText("$body $timerText".trim())
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
