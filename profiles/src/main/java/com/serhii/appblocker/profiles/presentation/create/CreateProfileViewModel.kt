package com.serhii.appblocker.profiles.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.usecase.CreateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetInstalledAppsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProfileViewModel(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProfileState())
    val state = _state.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            val apps = getInstalledAppsUseCase()
            _state.update { it.copy(installedApps = apps, isLoading = false) }
        }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun toggleAppSelection(packageName: String) {
        _state.update { state ->
            val newSelectedApps = if (state.selectedApps.contains(packageName)) {
                state.selectedApps - packageName
            } else {
                state.selectedApps + packageName
            }
            state.copy(selectedApps = newSelectedApps)
        }
    }

    fun createProfile() {
        val currentState = _state.value
        if (currentState.name.isBlank() || currentState.selectedApps.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching {
                createProfileUseCase(
                    name = currentState.name,
                    appPackages = currentState.selectedApps.toList()
                )
            }.onFailure {
                it.printStackTrace()
                _state.update { it.copy(isSaving = false) }
            }.onSuccess {
                _state.update { it.copy(isCreated = true, isSaving = false) }
            }
        }
    }
}

data class CreateProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val installedApps: List<AppInfo> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
    val isSaving: Boolean = false,
    val isCreated: Boolean = false,
)
