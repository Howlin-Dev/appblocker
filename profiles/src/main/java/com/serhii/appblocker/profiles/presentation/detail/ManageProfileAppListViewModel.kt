package com.serhii.appblocker.profiles.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.domain.usecase.GetInstalledAppsUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfileUiUseCase
import com.serhii.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageProfileAppListViewModel(
    private val getProfileUiUseCase: GetProfileUiUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ManageProfileAppListState())
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

    fun getProfile(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val profileUi = getProfileUiUseCase(id)
                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profileUi,
                        selectedApps = profileUi.blockedApps.map { it.packageName }.toSet()
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
