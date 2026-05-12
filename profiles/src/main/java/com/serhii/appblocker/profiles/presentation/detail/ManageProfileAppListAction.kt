package com.serhii.appblocker.profiles.presentation.detail

sealed interface ManageProfileAppListAction {
    data class AppSelected(val packageName: String): ManageProfileAppListAction
    object ApplyClick: ManageProfileAppListAction
    object BackClick: ManageProfileAppListAction
}