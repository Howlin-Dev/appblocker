package com.howlindev.appblocker.presentation.block

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.model.ActiveBlock
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.domain.repository.InstalledAppsRepository
import com.howlindev.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.howlindev.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockViewModel(
    private val savedStateHandle: SavedStateHandle,
    observeRemainingTimeUseCase: ObserveRemainingTimeUseCase,
    private val observeActiveBlockUseCase: ObserveActiveBlockUseCase,
    private val installedAppsRepository: InstalledAppsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BlockState())
    val state: StateFlow<BlockState> = _state.asStateFlow()

    val remainingTime: StateFlow<Long> =
        observeRemainingTimeUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 100L,
            )

    val isStillBlocked: StateFlow<Boolean> = combine(
        savedStateHandle.getStateFlow<String?>(BlockActivity.EXTRA_PACKAGE_NAME, null),
        savedStateHandle.getStateFlow<String?>(BlockActivity.EXTRA_WEBSITE_URL, null),
        observeActiveBlockUseCase(),
    ) { pkg, url, activeBlock ->
        if (activeBlock == null) return@combine false

        val isAppBlocked = pkg?.let {
            activeBlock.blockedPackages.contains(it)
        } ?: false

        val isWebsiteBlocked = url?.let { targetUrl ->
            activeBlock.blockedWebsites.any { blockedUrl ->
                targetUrl.contains(blockedUrl, ignoreCase = true)
            }
        } ?: false

        isAppBlocked || isWebsiteBlocked
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true,
    )

    init {
        observeActiveProfile()

        savedStateHandle.getStateFlow<String?>(BlockActivity.EXTRA_PACKAGE_NAME, null)
            .onEach { pkg ->
                pkg?.let { loadBlockedAppInfo(it) }
            }
            .launchIn(viewModelScope)

        savedStateHandle.getStateFlow<String?>(BlockActivity.EXTRA_WEBSITE_URL, null)
            .onEach { url ->
                _state.update { it.copy(blockedWebsite = url) }
            }
            .launchIn(viewModelScope)
    }

    fun updateBlockedPackage(packageName: String) {
        savedStateHandle[BlockActivity.EXTRA_PACKAGE_NAME] = packageName
    }

    fun updateBlockedWebsite(websiteUrl: String) {
        savedStateHandle[BlockActivity.EXTRA_WEBSITE_URL] = websiteUrl
    }

    private fun loadBlockedAppInfo(packageName: String) {
        viewModelScope.launch {
            _state.update { it.copy(blockedApp = null) }
            val appInfo = installedAppsRepository.getAppInfo(packageName)
            _state.update { it.copy(blockedApp = appInfo) }
        }
    }

    private fun observeActiveProfile() {
        observeActiveBlockUseCase()
            .onEach { activeBlock ->
                Log.d("observeActiveBlockUseCase", activeBlock.toString())
                _state.update { it.copy(activeBlock = activeBlock) }
            }
            .launchIn(viewModelScope)
    }
}

data class BlockState(
    val activeBlock: ActiveBlock? = null,
    val blockedApp: AppInfo? = null,
    val blockedWebsite: String? = null,
)
