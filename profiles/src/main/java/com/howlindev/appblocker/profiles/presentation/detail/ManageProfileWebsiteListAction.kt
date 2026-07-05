package com.howlindev.appblocker.profiles.presentation.detail

sealed interface ManageProfileWebsiteListAction {
    data class WebsiteSelected(val website: String) : ManageProfileWebsiteListAction
    data class CustomWebsiteAdded(val website: String) : ManageProfileWebsiteListAction
    data class WebsiteRemoved(val website: String) : ManageProfileWebsiteListAction
    data object ApplyClick : ManageProfileWebsiteListAction
    data object BackClick : ManageProfileWebsiteListAction
}
