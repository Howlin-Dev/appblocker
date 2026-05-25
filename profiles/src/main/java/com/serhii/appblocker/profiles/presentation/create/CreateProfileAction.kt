package com.serhii.appblocker.profiles.presentation.create

sealed interface CreateProfileAction {
    data class NameChange(val name: String): CreateProfileAction
    data class AppSelected(val packageName: String): CreateProfileAction
    data object CreateProfileClick: CreateProfileAction
    data object BackClick: CreateProfileAction
}
