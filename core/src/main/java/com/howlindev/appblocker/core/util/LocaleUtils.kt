package com.howlindev.appblocker.core.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleUtils {
    fun applyLocale(context: Context, languageTag: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocale)

        // Legacy/OEM-specific resource lookup satisfaction
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)

        // On API 33+, also explicitly set via LocaleManager to handle MIUI persistence better
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            try {
                localeManager?.applicationLocales = LocaleList.forLanguageTags(languageTag)
            } catch (e: Exception) {
                // Ignore potential framework issues on some OEMs
            }
        }

        // MIUI specific workaround for language change not applying immediately
        if (DeviceUtils.isMiui()) {
            val resources = context.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            // Also update application context resources for MIUI
            val appContext = context.applicationContext
            val appResources = appContext.resources
            val appConfig = appResources.configuration
            appConfig.setLocale(locale)
            appResources.updateConfiguration(appConfig, appResources.displayMetrics)
        }
    }

    fun getLocale(context: Context): String? {
        // Prefer LocaleManager on API 33+ for system-wide sync
        val locales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            localeManager?.applicationLocales
        } else {
            null
        }

        val frameworkTags = locales?.toLanguageTags()
        if (!frameworkTags.isNullOrBlank()) {
            return frameworkTags.split(",").firstOrNull()
        }

        val appCompatTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (!appCompatTags.isBlank()) {
            return appCompatTags.split(",").firstOrNull()
        }

        return null
    }
}
