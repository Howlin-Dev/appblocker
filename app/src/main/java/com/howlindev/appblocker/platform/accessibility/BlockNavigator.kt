package com.howlindev.appblocker.platform.accessibility

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import com.howlindev.appblocker.presentation.block.BlockActivity

object BlockNavigator {

    fun navigateBrowserAway(context: Context, browserPackage: String) {
        if (browserPackage.isBlank()) return
        try {
            val intent = Intent(Intent.ACTION_VIEW, "https://www.google.com".toUri()).apply {
                setPackage(browserPackage)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP,
                )
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    fun launchBlockScreen(context: Context, packageName: String, websiteUrl: String? = null) {
        val intent = Intent(context, BlockActivity::class.java).apply {
            putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
            websiteUrl?.let { putExtra(BlockActivity.EXTRA_WEBSITE_URL, it) }
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION,
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val options = ActivityOptions.makeBasic()
            options.setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            try {
                pendingIntent.send(options.toBundle())
            } catch (e: Exception) {
                context.startActivity(intent)
            }
        } else {
            context.startActivity(intent)
        }
    }
}
