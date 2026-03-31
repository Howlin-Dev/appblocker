package com.serhii.appblocker.profiles.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.repository.BlockRepository
import com.serhii.appblocker.core.domain.repository.TimerRepository
import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileListViewModel(
    private val profilesRepository: ProfilesRepository,
    private val installedAppsRepository: InstalledAppsRepository,
    private val blockRepository: BlockRepository,
    private val timerRepository: TimerRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesListState())
    val state: StateFlow<ProfilesListState> = _state.asStateFlow()

    val remainingTime: StateFlow<Long> =
        timerRepository.observeRemainingTime()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0L
            )

    init {
        getProfiles()
        subscribeToActiveProfiles()
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

    fun subscribeToActiveProfiles() {
        blockRepository.activeBlock
            .onEach { activeBlock ->
                _state.update { it.copy(activeProfileId = activeBlock?.profileId) }
            }
            .launchIn(viewModelScope)
    }

    fun toggleProfileActivation(profile: ProfileUi) {
        if(_state.value.activeProfileId == profile.id) {
            deactivateProfile()
        } else {
            activateProfile(profile)
        }
    }

    private fun activateProfile(profile: ProfileUi) {
        viewModelScope.launch {
            timerRepository.startTimer(180000)
            blockRepository.activateProfile(
                profileId = profile.id,
                blockedPackages = profile.blockedApps.map { app -> app.packageName }
            )
        }
    }

    private fun deactivateProfile() {
        viewModelScope.launch {
            blockRepository.deactivate()
            _state.update { it.copy(activeProfileId = null) }
        }
    }
}

data class ProfilesListState(
    val isLoading: Boolean = false,
    val profiles: List<ProfileUi> = emptyList(),
    val activeProfileId: Long? = null,
)
