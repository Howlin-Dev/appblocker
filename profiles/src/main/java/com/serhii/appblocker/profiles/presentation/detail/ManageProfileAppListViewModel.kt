package com.serhii.appblocker.profiles.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.usecase.GetProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toDomain
import com.serhii.appblocker.profiles.presentation.list.model.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageProfileAppListViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val installedAppsRepository: InstalledAppsRepository,
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

    fun getProfile(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val profile = getProfileUseCase(id)
                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profile.toUi(installedAppsRepository),
                        selectedApps = profile.appPackages.toSet()
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
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

    fun applyChanges() {
        viewModelScope.launch {
            runCatching {
                _state.value.profile?.toDomain()
                    ?.copy(appPackages = _state.value.selectedApps.toList())?.let {
                    updateProfileUseCase(it)
                }
                _state.update { it.copy(isLoading = false, isSaved = true) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class ManageProfileAppListState(
    val isLoading: Boolean = false,
    val profile: ProfileUi? = null,
    val installedApps: List<AppInfo> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
    val isSaved: Boolean = false,
)
