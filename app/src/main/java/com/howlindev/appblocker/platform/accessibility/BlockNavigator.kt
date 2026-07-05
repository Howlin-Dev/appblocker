package com.howlindev.appblocker.platform.accessibility

import android.content.Context
import android.content.Intent
import android.util.Log
import com.howlindev.appblocker.presentation.block.BlockActivity
import androidx.core.net.toUri

object BlockNavigator {

    fun navigateBrowserAway(context: Context, browserPackage: String) {
        if (browserPackage.isBlank()) return
        try {
            // Force the browser to navigate to a safe page and clear the task stack
            val intent = Intent(Intent.ACTION_VIEW, "https://www.google.com".toUri()).apply {
                setPackage(browserPackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or 
                         Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                         Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("BlockNavigator", "Failed to navigate browser away", e)
        }
    }

    fun launchBlockScreen(context: Context, packageName: String, websiteUrl: String? = null) {
        val intent = Intent(context, BlockActivity::class.java).apply {
            putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
            websiteUrl?.let { putExtra(BlockActivity.EXTRA_WEBSITE_URL, it) }
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP,
            )
        }
        Log.d("BlockNavigator", "Launching block screen for $packageName, website: $websiteUrl")
        context.startActivity(intent)
    }
}
