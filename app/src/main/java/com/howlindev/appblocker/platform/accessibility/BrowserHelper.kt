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
    )

    fun isBrowser(packageName: String): Boolean {
        return browsers.contains(packageName)
    }
}
