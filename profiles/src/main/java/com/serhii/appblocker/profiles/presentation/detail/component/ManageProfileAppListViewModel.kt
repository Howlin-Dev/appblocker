package com.serhii.appblocker.profiles.presentation.detail.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.core.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageProfileAppListViewModel(
    private val installedAppsRepository: InstalledAppsRepository,
    private val profilesRepository: ProfilesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ManageProfileAppListState())
    val state = _state.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = installedAppsRepository.getInstalledApps()
            _state.update { it.copy(installedApps = apps) }
        }
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
}

data class ManageProfileAppListState(
    val installedApps: List<AppInfo> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
)