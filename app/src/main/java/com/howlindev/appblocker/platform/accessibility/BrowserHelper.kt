package com.howlindev.appblocker.platform.accessibility

object BrowserHelper {
    private val browsers = listOf(
        "com.android.chrome",
        "org.mozilla.firefox",
        "com.microsoft.emmx",
        "com.opera.browser",
        "com.duckduckgo.mobile.android",
        "com.brave.browser",
        "com.sec.android.app.sbrowser",
        "com.mi.globalbrowser",
        "com.vivaldi.browser",
        "com.android.browser",
        "com.huawei.browser",
        "com.heytap.browser",
        "com.sec.android.app.sbrowser.beta",
    )

    fun isBrowser(packageName: String): Boolean {
        return browsers.contains(packageName)
    }
}
