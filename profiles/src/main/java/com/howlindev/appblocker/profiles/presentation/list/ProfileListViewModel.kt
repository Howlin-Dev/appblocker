package com.howlindev.appblocker.profiles.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.howlindev.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import com.howlindev.appblocker.permissions.domain.usecase.RequestPermissionUseCase
import com.howlindev.appblocker.profiles.domain.usecase.ActivateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.DeactivateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfilesUiUseCase
import com.howlindev.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import com.howlindev.appblocker.profiles.presentation.list.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private val getMissingPermissionsUseCase: GetMissingPermissionsUseCase,
    private val requestPermissionUseCase: RequestPermissionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesListState())
    val state: StateFlow<ProfilesListState> = _state.asStateFlow()

    val remainingTime: StateFlow<Long> =
        observeRemainingTimeUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0L,
            )

    init {
        observeState()
    }

    fun checkPermissions() {
        viewModelScope.launch {
            _state.update { it.copy(missingPermissions = getMissingPermissionsUseCase()) }
            kotlinx.coroutines.delay(500)
            _state.update { it.copy(missingPermissions = getMissingPermissionsUseCase()) }
        }
    }

    fun requestPermission(permission: RequiredPermission) {
        requestPermissionUseCase(permission)
    }

    private fun observeState() {
        viewModelScope.launch {
            combine(
                getProfilesUiUseCase(),
                observeActiveBlockUseCase(),
            ) { profilesUi, activeBlock ->
                val activeProfile = profilesUi.find { it.id == activeBlock?.profileId }
                val inactiveProfiles = profilesUi.filter { it.id != activeBlock?.profileId }

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        inactiveProfiles = inactiveProfiles,
                        activeProfile = activeProfile,
                    )
                }
            }.collect {}
        }
    }

    fun toggleProfileActivation(profile: ProfileUi) {
        viewModelScope.launch {
            if (state.value.activeProfile?.id == profile.id) {
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
    val missingPermissions: List<RequiredPermission> = emptyList(),
)
