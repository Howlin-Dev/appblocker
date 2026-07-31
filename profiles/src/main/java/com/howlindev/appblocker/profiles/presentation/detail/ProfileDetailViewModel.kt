package com.howlindev.appblocker.profiles.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.howlindev.appblocker.profiles.domain.usecase.CreateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.DeleteProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfileUiUseCase
import com.howlindev.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import com.howlindev.appblocker.profiles.presentation.list.model.toDomain
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.usecase.DeleteScheduleEventUseCase
import com.howlindev.appblocker.schedule.domain.usecase.GetScheduleEventsByProfileIdUseCase
import com.howlindev.appblocker.schedule.domain.usecase.SaveScheduleEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val getProfileUiUseCase: GetProfileUiUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val observeActiveBlockUseCase: ObserveActiveBlockUseCase,
    private val getScheduleEventsByProfileIdUseCase: GetScheduleEventsByProfileIdUseCase,
    private val saveScheduleEventUseCase: SaveScheduleEventUseCase,
    private val deleteScheduleEventUseCase: DeleteScheduleEventUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileDetailState())
    val state: StateFlow<ProfileDetailState> = _state

    fun getProfile(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val profileUi = getProfileUiUseCase(id)
                _state.update { it.copy(isLoading = false, profile = profileUi) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }

        viewModelScope.launch {
            getScheduleEventsByProfileIdUseCase(id).collectLatest { events ->
                _state.update { it.copy(scheduleEvents = events) }
            }
        }

        viewModelScope.launch {
            observeActiveBlockUseCase().collectLatest { activeBlock ->
                val isProfileActive = activeBlock?.profileId == id ||
                    activeBlock?.scheduledProfileEndTimes?.containsKey(id) == true
                _state.update { it.copy(isProfileActive = isProfileActive) }
            }
        }
    }

    fun updateProfileName(newName: String) {
        _state.value.profile?.copy(name = newName)?.let {
            updateProfile(it)
        }
    }

    private fun updateProfile(profile: ProfileUi) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                updateProfileUseCase(profile.toDomain())
                _state.update { it.copy(isLoading = false, profile = profile) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                deleteProfileUseCase(_state.value.profile!!.id)
                _state.update { it.copy(isLoading = false, isProfileDeleted = true) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveScheduleEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            saveScheduleEventUseCase(event)
        }
    }

    fun deleteScheduleEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            deleteScheduleEventUseCase(event)
        }
    }

    fun duplicateProfile(name: String) {
        val currentProfile = _state.value.profile ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                createProfileUseCase(
                    name = name,
                    appPackages = currentProfile.blockedApps.map { it.packageName },
                    blockedWebsites = currentProfile.blockedWebsites,
                )
                _state.update { it.copy(isLoading = false, isProfileDuplicated = true) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class ProfileDetailState(
    val isLoading: Boolean = false,
    val profile: ProfileUi? = null,
    val isProfileDeleted: Boolean = false,
    val isProfileActive: Boolean = false,
    val scheduleEvents: List<ScheduleEvent> = emptyList(),
    val isProfileDuplicated: Boolean = false,
)
