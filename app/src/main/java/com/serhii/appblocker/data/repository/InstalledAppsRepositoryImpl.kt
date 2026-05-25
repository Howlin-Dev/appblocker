package com.serhii.appblocker.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.LruCache
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.core.domain.repository.InstalledAppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstalledAppsRepositoryImpl(
    private val context: Context
) : InstalledAppsRepository {

    private val pm = context.packageManager
    private val cache = LruCache<String, AppInfo>(100)

    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager

        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfoList = pm.queryIntentActivities(intent, 0)

        val apps = resolveInfoList
            .map { resolveInfo ->
                val appInfo = resolveInfo.activityInfo.applicationInfo
                AppInfo(
                    packageName = appInfo.packageName,
                    name = resolveInfo.loadLabel(pm).toString(),
                    icon = resolveInfo.loadIcon(pm)
                )
            }
            .filterNot { it.packageName == context.packageName }
            .sortedBy { it.name }

        return apps
    }

    override suspend fun getAppInfo(packageName: String): AppInfo = withContext(Dispatchers.IO) {
        cache.get(packageName)?.let { return@withContext it }

        val appInfo = pm.getApplicationInfo(packageName, 0)
        val icon = pm.getApplicationIcon(appInfo)
        val name = pm.getApplicationLabel(appInfo).toString()

        AppInfo(packageName, name, icon).also { cache.put(packageName, it) }
    }
}