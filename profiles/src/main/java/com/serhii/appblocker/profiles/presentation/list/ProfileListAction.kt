package com.serhii.appblocker.profiles.presentation.list

sealed interface ProfileListAction {
    object AddClick: ProfileListAction
    data class ProfileClick(val id: Long): ProfileListAction
}