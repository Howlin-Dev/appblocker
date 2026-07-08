package com.howlindev.appblocker.platform.accessibility

import android.view.accessibility.AccessibilityNodeInfo

object UrlFinder {
    private val browserAddressBarIds = listOf(
        "com.android.chrome:id/url_bar",
        "org.mozilla.firefox:id/url_bar_title",
        "com.microsoft.emmx:id/url_bar",
        "com.brave.browser:id/url_bar",
        "com.sec.android.app.sbrowser:id/location_bar_edit_text",
        "com.duckduckgo.mobile.android:id/omnibar_text",
        "com.opera.browser:id/url_field",
    )

    fun findUrlInNode(node: AccessibilityNodeInfo): String? {
        var knownAddressBarFound = false
        for (id in browserAddressBarIds) {
            val nodes = node.findAccessibilityNodeInfosByViewId(id)
            if (nodes.isNotEmpty()) {
                knownAddressBarFound = true
                val urlNode = nodes[0]
                val isFocused = urlNode.isFocused
                val text = urlNode.text?.toString()

                nodes.forEach { it.recycle() }

                if (isFocused) {
                    return null
                }

                if (!text.isNullOrBlank() && (text.contains(".") || text.contains("://"))) {
                    return text
                }
            }
        }

        if (knownAddressBarFound) return null

        return findUrlByHeuristics(node)
    }

    private fun findUrlByHeuristics(node: AccessibilityNodeInfo, isInsideList: Boolean = false): String? {
        val className = node.className?.toString() ?: ""
        val currentIsList = isInsideList ||
            className.contains("ListView") ||
            className.contains("RecyclerView") ||
            className.contains("AbsListView") ||
            className.contains("List")

        if (node.className == "android.widget.EditText" && node.isFocused) {
            return null
        }

        if (!currentIsList && (node.className == "android.widget.EditText" || node.className == "android.widget.TextView")) {
            val text = node.text?.toString()
            if (text != null && text.length > 3 && (text.contains(".") || text.contains("://"))) {
                return text
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findUrlByHeuristics(child, currentIsList)
            child.recycle()
            if (result != null) return result
        }
        return null
    }
}
