package com.howlindev.appblocker.profiles.presentation.list.component

sealed interface ProfileDetailAction {
    data object BackClick : ProfileDetailAction
    data object DeleteProfile : ProfileDetailAction
    data object ManageAppListClick : ProfileDetailAction
    data object ManageWebsiteListClick : ProfileDetailAction
    data class ProfileNameChanged(val name: String) : ProfileDetailAction
}
