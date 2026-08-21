package com.howlindev.appblocker.core.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleUtils {
    fun applyLocale(context: Context, languageTag: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocale)

        // On API 33+, also explicitly set via LocaleManager to handle MIUI persistence better
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            localeManager?.applicationLocales = LocaleList.forLanguageTags(languageTag)
        }
    }

    fun getLocale(context: Context): String? {
        // Prefer LocaleManager on API 33+ for system-wide sync
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as? LocaleManager
            val locales = localeManager?.applicationLocales
            if (locales != null && !locales.isEmpty) locales.toLanguageTags() else null
        } else {
            val locales = AppCompatDelegate.getApplicationLocales()
            if (!locales.isEmpty) locales.toLanguageTags() else null
        }
    }
}
