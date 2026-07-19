package com.howlindev.appblocker.profiles.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.profiles.domain.usecase.CreateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.DeleteProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfileUiUseCase
import com.howlindev.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import com.howlindev.appblocker.profiles.presentation.list.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val getProfileUiUseCase: GetProfileUiUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
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

    fun duplicateProfile(name: String) {
        val currentProfile = _state.value.profile ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                createProfileUseCase(
                    name = name,
                    appPackages = currentProfile.blockedApps.map { it.packageName },
                    blockedWebsites = currentProfile.blockedWebsites
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
    val isProfileDuplicated: Boolean = false,
)
