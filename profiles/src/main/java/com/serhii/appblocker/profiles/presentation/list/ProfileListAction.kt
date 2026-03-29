package com.serhii.appblocker.profiles.presentation.list

sealed interface ProfileListAction {
    object CreateClick: ProfileListAction
    data class ProfileClick(val id: Long): ProfileListAction
}