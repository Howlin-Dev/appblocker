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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
            kotlinx.coroutines.delay(500.milliseconds)
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
                val timedProfileId = activeBlock?.profileId
                val scheduledEndTimes = activeBlock?.scheduledProfileEndTimes ?: emptyMap()

                val activeProfiles = profilesUi.filter { profile ->
                    profile.id == timedProfileId || scheduledEndTimes.containsKey(profile.id)
                }.map { profile ->
                    profile.copy(
                        scheduledEndTime = scheduledEndTimes[profile.id],
                        isManuallyActive = profile.id == timedProfileId,
                    )
                }

                val inactiveProfiles = profilesUi.filter { profile ->
                    profile.id != timedProfileId && !scheduledEndTimes.containsKey(profile.id)
                }

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        inactiveProfiles = inactiveProfiles,
                        activeProfiles = activeProfiles,
                        isManualProfileActive = timedProfileId != null,
                    )
                }
            }.collect {}
        }
    }

    fun toggleProfileActivation(profile: ProfileUi) {
        viewModelScope.launch {
            val isManuallyActive = observeActiveBlockUseCase().first()?.profileId == profile.id
            if (isManuallyActive) {
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
    val activeProfiles: List<ProfileUi> = emptyList(),
    val isManualProfileActive: Boolean = false,
    val missingPermissions: List<RequiredPermission> = emptyList(),
)
