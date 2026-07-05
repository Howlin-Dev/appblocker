package com.howlindev.appblocker.profiles.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.profiles.domain.usecase.AddSuggestedWebsiteUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetProfileUiUseCase
import com.howlindev.appblocker.profiles.domain.usecase.GetSuggestedWebsitesUseCase
import com.howlindev.appblocker.profiles.domain.usecase.RemoveSuggestedWebsiteUseCase
import com.howlindev.appblocker.profiles.domain.usecase.UpdateProfileUseCase
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import com.howlindev.appblocker.profiles.presentation.list.model.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManageProfileWebsiteListViewModel(
    private val getProfileUiUseCase: GetProfileUiUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getSuggestedWebsitesUseCase: GetSuggestedWebsitesUseCase,
    private val addSuggestedWebsiteUseCase: AddSuggestedWebsiteUseCase,
    private val removeSuggestedWebsiteUseCase: RemoveSuggestedWebsiteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ManageProfileWebsiteListState())
    val state = _state.asStateFlow()

    init {
        observeSuggestedWebsites()
    }

    private fun observeSuggestedWebsites() {
        viewModelScope.launch {
            getSuggestedWebsitesUseCase().collect { websites ->
                _state.update { it.copy(suggestedWebsites = websites) }
            }
        }
    }

    fun getProfile(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val profileUi = getProfileUiUseCase(id)
                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profileUi,
                        selectedWebsites = profileUi.blockedWebsites.toSet(),
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
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

    fun applyChanges() {
        viewModelScope.launch {
            runCatching {
                _state.value.profile?.toDomain()
                    ?.copy(blockedWebsites = _state.value.selectedWebsites.toList())?.let {
                        updateProfileUseCase(it)
                    }
                _state.update { it.copy(isLoading = false, isSaved = true) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class ManageProfileWebsiteListState(
    val isLoading: Boolean = false,
    val profile: ProfileUi? = null,
    val suggestedWebsites: List<String> = emptyList(),
    val selectedWebsites: Set<String> = emptySet(),
    val isSaved: Boolean = false,
)
