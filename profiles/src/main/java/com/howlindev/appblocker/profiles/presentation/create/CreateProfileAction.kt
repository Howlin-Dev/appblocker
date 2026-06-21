package com.howlindev.appblocker.profiles.presentation.create

sealed interface CreateProfileAction {
    data class NameChange(val name: String) : CreateProfileAction
    data class AppSelected(val packageName: String) : CreateProfileAction
    data class WebsiteSelected(val website: String) : CreateProfileAction
    data class CustomWebsiteAdded(val website: String) : CreateProfileAction
    data class WebsiteRemoved(val website: String) : CreateProfileAction
    data object CreateProfileClick : CreateProfileAction
    data object BackClick : CreateProfileAction
}
