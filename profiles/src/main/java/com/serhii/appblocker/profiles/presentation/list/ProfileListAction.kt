package com.serhii.appblocker.profiles.presentation.list

import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi

sealed interface ProfileListAction {
    data object CreateClick : ProfileListAction
    data object SettingsClick : ProfileListAction
    data class ProfileClick(val id: Long) : ProfileListAction
    data class TimerChange(val profileUi: ProfileUi, val newTime: Long?) : ProfileListAction
    data class ToggleProfileActivation(val profile: ProfileUi) : ProfileListAction
}
