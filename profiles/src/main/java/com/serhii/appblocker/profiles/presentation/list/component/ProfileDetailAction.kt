package com.serhii.appblocker.profiles.presentation.list.component

sealed interface ProfileDetailAction {
    object BackClick: ProfileDetailAction
    object DeleteProfile: ProfileDetailAction
    object ManageListClick: ProfileDetailAction
    data class ProfileNameChanged(val name: String): ProfileDetailAction
}