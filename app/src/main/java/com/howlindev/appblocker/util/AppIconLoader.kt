package com.howlindev.appblocker.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache
import com.howlindev.appblocker.core.util.AppIconProvider

class AppIconLoader(context: Context) : AppIconProvider {
    private val pm = context.packageManager
    private val cache = LruCache<String, Drawable>(100)

    override fun getIcon(packageName: String): Drawable? {
        cache.get(packageName)?.let { return it }
        return try {
            val icon = pm.getApplicationIcon(packageName)
            cache.put(packageName, icon)
            icon
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

