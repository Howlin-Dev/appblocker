package com.serhii.appblocker.profiles.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProfileViewModel(
    private val installedAppsRepository: InstalledAppsRepository,
    private val profilesRepository: ProfilesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProfileState())
    val state = _state.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = installedAppsRepository.getInstalledApps()
            _state.update { it.copy(installedApps = apps) }
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
            val profile = Profile(
                id = 0,
                name = currentState.name,
                description = "",
                blockedAppsPackageNames = currentState.selectedApps.toList()
            )
            runCatching {
                profilesRepository.insert(profile)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                _state.update { it.copy(isCreated = true) }
            }
        }
    }
}

data class CreateProfileState(
    val name: String = "",
    val installedApps: List<AppInfo> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
    val isSaving: Boolean = false,
    val isCreated: Boolean = false
)
