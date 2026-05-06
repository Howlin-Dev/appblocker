package com.serhii.appblocker.profiles.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.profiles.data.model.toEntity
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.usecase.DeleteProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.GetProfileUseCase
import com.serhii.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toDomain
import com.serhii.appblocker.profiles.presentation.list.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val installedAppsRepository: InstalledAppsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileDetailState())
    val state: StateFlow<ProfileDetailState> = _state

    fun getProfile(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val profile = getProfileUseCase(id)
                _state.update { it.copy(isLoading = false, profile = profile.toUi(installedAppsRepository)) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateProfile(profile: ProfileUi) {
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
}

data class ProfileDetailState(
    val isLoading: Boolean = false,
    val profile: ProfileUi? = null,
    val isProfileDeleted: Boolean = false,
)