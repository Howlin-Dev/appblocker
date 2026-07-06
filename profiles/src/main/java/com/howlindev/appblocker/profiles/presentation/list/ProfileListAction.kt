package com.howlindev.appblocker.profiles.presentation.list

import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi

sealed interface ProfileListAction {
    data object CreateClick : ProfileListAction
    data object SettingsClick : ProfileListAction
    data class ProfileClick(val id: Long) : ProfileListAction
    data class TimerChange(val profileUi: ProfileUi, val newTime: Long?) : ProfileListAction
    data class ToggleProfileActivation(val profile: ProfileUi) : ProfileListAction
    data class GrantPermission(val permission: RequiredPermission) : ProfileListAction
    data class ShowPermissionInfo(val permission: RequiredPermission) : ProfileListAction
}
