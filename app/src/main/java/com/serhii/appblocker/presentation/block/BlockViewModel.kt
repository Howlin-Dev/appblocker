package com.serhii.appblocker.presentation.block

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhii.appblocker.core.domain.model.ActiveBlock
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.core.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.core.domain.usecase.ObserveActiveBlockUseCase
import com.serhii.appblocker.core.domain.usecase.ObserveRemainingTimeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        observeActiveProfile()
        
        savedStateHandle.getStateFlow<String?>(BlockActivity.EXTRA_PACKAGE_NAME, null)
            .onEach { pkg ->
                pkg?.let { loadBlockedAppInfo(it) }
            }
            .launchIn(viewModelScope)
    }

    fun updateBlockedPackage(packageName: String) {
        savedStateHandle[BlockActivity.EXTRA_PACKAGE_NAME] = packageName
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
)
