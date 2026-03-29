package com.serhii.appblocker.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository

class InstalledAppsRepositoryImpl(
    private val context: Context
) : InstalledAppsRepository {

    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager

        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        return packages
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map {
                AppInfo(
                    packageName = it.packageName,
                    name = pm.getApplicationLabel(it).toString()
                )
            }
            .sortedBy { it.name.lowercase() }
    }
}