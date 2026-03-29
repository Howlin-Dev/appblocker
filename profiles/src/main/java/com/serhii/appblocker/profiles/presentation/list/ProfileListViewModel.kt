package com.serhii.appblocker.profiles.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class ProfileListViewModel(
    private val profilesRepository: ProfilesRepository,
    private val installedAppsRepository: InstalledAppsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesListState())
    val state: StateFlow<ProfilesListState> = _state.asStateFlow()

    init {
        getProfiles()
    }

    private fun getProfiles() {
        profilesRepository.getAll()
            .onStart {
                _state.update { it.copy(isLoading = true) }
            }
            .onEach { profiles ->
                _state.update {
                    it.copy(
                        profiles = profiles.map { p ->
                            ProfileUi(
                                id = p.id,
                                name = p.name,
                                description = p.description,
                                blockedApps = p.blockedAppsPackageNames.map { packageName ->
                                    installedAppsRepository.getAppInfo(
                                        packageName
                                    )
                                }
                            )
                        },
                        isLoading = false
                    )
                }
            }
            .catch {
                _state.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }
}

data class ProfilesListState(
    val isLoading: Boolean = false,
    val profiles: List<ProfileUi> = emptyList(),
)
