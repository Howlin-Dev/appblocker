package com.serhii.appblocker.profiles.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfilesUiUseCase
import com.serhii.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.serhii.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import com.serhii.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileListViewModel(
    observeRemainingTimeUseCase: ObserveRemainingTimeUseCase,
    private val getProfilesUiUseCase: GetProfilesUiUseCase,
    private val observeActiveBlockUseCase: ObserveActiveBlockUseCase,
    private val activateProfileUseCase: ActivateProfileUseCase,
    private val deactivateProfileUseCase: DeactivateProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
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
        observeState()
    }

    private fun observeState() {
        combine(
            getProfilesUiUseCase(),
            observeActiveBlockUseCase()
        ) { profilesUi, activeBlock ->

            val activeProfile = profilesUi.find {
                it.id == activeBlock?.profileId
            }

            val inactiveProfiles = profilesUi.filter {
                it.id != activeBlock?.profileId
            }

            ProfilesListState(
                isLoading = false,
                activeProfile = activeProfile,
                inactiveProfiles = inactiveProfiles
            )
        }
            .onStart {
                _state.update { it.copy(isLoading = true) }
            }
            .catch {
                _state.update { it.copy(isLoading = false) }
            }
            .onEach { newState ->
                _state.value = newState
            }
            .launchIn(viewModelScope)
    }

    fun toggleProfileActivation(profile: ProfileUi) {
        viewModelScope.launch {
            if (_state.value.activeProfile?.id == profile.id) {
                deactivateProfileUseCase()
            } else {
                activateProfileUseCase(profile.toDomain())
            }
        }
    }

    fun updateProfileTimer(profileUi: ProfileUi, newTime: Long?) {
        updateProfile(profileUi.copy(durationMillis = newTime))
    }

    private fun updateProfile(profile: ProfileUi) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                updateProfileUseCase(profile.toDomain())
//                _state.update { it.copy(isLoading = false, profiles = _state.value.profiles.) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class ProfilesListState(
    val isLoading: Boolean = false,
    val inactiveProfiles: List<ProfileUi> = emptyList(),
    val activeProfile: ProfileUi? = null,
)
