package com.serhii.appblocker.profiles.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfilesUseCase
import com.serhii.appblocker.profiles.domain.usecase.ObserveActiveProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.ObserveRemainingTimeUseCase
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toDomain
import com.serhii.appblocker.profiles.presentation.list.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileListViewModel(
    observeRemainingTimeUseCase: ObserveRemainingTimeUseCase,
    private val getProfilesUseCase: GetProfilesUseCase,
    private val observeActiveProfileUseCase: ObserveActiveProfileUseCase,
    private val activateProfileUseCase: ActivateProfileUseCase,
    private val deactivateProfileUseCase: DeactivateProfileUseCase,
    private val installedAppsRepository: InstalledAppsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesListState())
    val state: StateFlow<ProfilesListState> = _state.asStateFlow()

    val remainingTime: StateFlow<Long> =
        observeRemainingTimeUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0L
            )

    init {
        observeProfiles()
        observeActiveProfile()
    }

    private fun observeProfiles() {
        getProfilesUseCase()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .map { profiles ->
                profiles.map { it.toUi(installedAppsRepository) }
            }
            .onEach { profilesUi ->
                _state.update {
                    it.copy(
                        profiles = profilesUi,
                        isLoading = false
                    )
                }
            }
            .catch {
                _state.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeActiveProfile() {
        observeActiveProfileUseCase()
            .onEach { activeProfileId ->
                _state.update { it.copy(activeProfileId = activeProfileId) }
            }
            .launchIn(viewModelScope)
    }

    fun toggleProfileActivation(profile: ProfileUi) {
        viewModelScope.launch {
            if (_state.value.activeProfileId == profile.id) {
                deactivateProfileUseCase()
            } else {
                activateProfileUseCase(profile.toDomain())
            }
        }
    }
}

data class ProfilesListState(
    val isLoading: Boolean = false,
    val profiles: List<ProfileUi> = emptyList(),
    val activeProfileId: Long? = null,
)
