package com.howlindev.appblocker.profiles.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.profiles.domain.usecase.AddSuggestedWebsiteUseCase
import com.howlindev.appblocker.profiles.domain.usecase.CreateProfileUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetInstalledAppsUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetSuggestedWebsitesUseCase
import com.howlindev.appblocker.profiles.domain.usecase.RemoveSuggestedWebsiteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProfileViewModel(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
    private val getSuggestedWebsitesUseCase: GetSuggestedWebsitesUseCase,
    private val addSuggestedWebsiteUseCase: AddSuggestedWebsiteUseCase,
    private val removeSuggestedWebsiteUseCase: RemoveSuggestedWebsiteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProfileState())
    val state = _state.asStateFlow()

    init {
        loadInstalledApps()
        observeSuggestedWebsites()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            val apps = getInstalledAppsUseCase()
            _state.update { it.copy(installedApps = apps, isLoading = false) }
        }
    }

    private fun observeSuggestedWebsites() {
        viewModelScope.launch {
            getSuggestedWebsitesUseCase().collect { websites ->
                _state.update { it.copy(suggestedWebsites = websites) }
            }
        }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
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

    fun toggleWebsiteSelection(website: String) {
        _state.update { state ->
            val newSelectedWebsites = if (state.selectedWebsites.contains(website)) {
                state.selectedWebsites - website
            } else {
                state.selectedWebsites + website
            }
            state.copy(selectedWebsites = newSelectedWebsites)
        }
    }

    fun addCustomWebsite(website: String) {
        if (website.isBlank()) return
        val trimmedWebsite = website.trim().lowercase()
        viewModelScope.launch {
            addSuggestedWebsiteUseCase(trimmedWebsite)
            toggleWebsiteSelection(trimmedWebsite)
        }
    }

    fun removeSuggestedWebsite(website: String) {
        viewModelScope.launch {
            removeSuggestedWebsiteUseCase(website)
            if (_state.value.selectedWebsites.contains(website)) {
                toggleWebsiteSelection(website)
            }
        }
    }

    fun createProfile() {
        val currentState = _state.value
        if (currentState.name.isBlank()) return
        if (currentState.selectedApps.isEmpty() && currentState.selectedWebsites.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching {
                createProfileUseCase(
                    name = currentState.name,
                    appPackages = currentState.selectedApps.toList(),
                    blockedWebsites = currentState.selectedWebsites.toList()
                )
            }.onFailure {
                it.printStackTrace()
                _state.update { it.copy(isSaving = false) }
            }.onSuccess {
                _state.update { it.copy(isCreated = true, isSaving = false) }
            }
        }
    }
}

data class CreateProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val installedApps: List<AppInfo> = emptyList(),
    val suggestedWebsites: List<String> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
    val selectedWebsites: Set<String> = emptySet(),
    val isSaving: Boolean = false,
    val isCreated: Boolean = false,
)
