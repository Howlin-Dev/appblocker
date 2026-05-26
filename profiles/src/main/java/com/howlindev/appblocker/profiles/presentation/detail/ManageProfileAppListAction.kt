package com.howlindev.appblocker.profiles.presentation.detail

sealed interface ManageProfileAppListAction {
    data class AppSelected(val packageName: String) : ManageProfileAppListAction
    data object ApplyClick : ManageProfileAppListAction
    data object BackClick : ManageProfileAppListAction
}

