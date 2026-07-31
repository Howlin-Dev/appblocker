package com.howlindev.appblocker.profiles.presentation.list

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.howlindev.appblocker.core.presentation.component.ConfirmDialog
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.core.presentation.util.LocalShimmerTransition
import com.howlindev.appblocker.core.util.millisToTimeString
import com.howlindev.appblocker.core.util.millisToTimerString
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.presentation.component.PermissionListContainerCard
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.list.component.ActiveProfileListItem
import com.howlindev.appblocker.profiles.presentation.list.component.ProfileListItem
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import org.koin.androidx.compose.koinViewModel
import com.howlindev.appblocker.permissions.R as PermissionR

@Composable
fun ProfileListScreen(
    onCreateClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSchedulerClick: () -> Unit,
    onProfileClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val remainingMillis by viewModel.remainingTime.collectAsState()
    val pendingProfileForActivation = remember { mutableStateOf<ProfileUi?>(null) }
    val infoPermission = remember { mutableStateOf<RequiredPermission?>(null) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val formattedTime = remember(remainingMillis, context) {
        remainingMillis.millisToTimerString(context)
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.checkPermissions()
        }
    }

    val shimmerTransition = rememberInfiniteTransition(label = "shimmer_sync")

    CompositionLocalProvider(LocalShimmerTransition provides shimmerTransition) {
        ProfileListScreenContent(
            modifier = modifier.fillMaxSize(),
            inactiveProfiles = state.inactiveProfiles,
            activeProfiles = state.activeProfiles,
            isManualProfileActive = state.isManualProfileActive,
            missingPermissions = state.missingPermissions,
            formattedTimeRemaining = formattedTime,
            onAction = { action ->
                when (action) {
                    ProfileListAction.CreateClick -> onCreateClick()
                    ProfileListAction.SettingsClick -> onSettingsClick()
                    ProfileListAction.SchedulerClick -> onSchedulerClick()
                    is ProfileListAction.ProfileClick -> onProfileClick(action.id)
                    is ProfileListAction.ToggleProfileActivation -> {
                        if (action.profile.durationMillis == null) {
                            viewModel.toggleProfileActivation(action.profile)
                        } else {
                            pendingProfileForActivation.value = action.profile
                        }
                    }

                    is ProfileListAction.TimerChange -> viewModel.updateProfileTimer(
                        action.profileUi,
                        action.newTime,
                    )

                    is ProfileListAction.GrantPermission -> viewModel.requestPermission(action.permission)
                    is ProfileListAction.ShowPermissionInfo -> {
                        infoPermission.value = action.permission
                    }
                }
            },
        )
    }

    if (infoPermission.value != null) {
        val permission = infoPermission.value!!
        ConfirmDialog(
            onConfirm = {
                viewModel.requestPermission(permission)
                infoPermission.value = null
            },
            onCancel = { infoPermission.value = null },
            title = stringResource(permission.titleRes),
            text = stringResource(permission.subtitleRes),
            confirmButtonText = stringResource(PermissionR.string.permission_grant_button),
            cancelButtonText = stringResource(PermissionR.string.permission_dismiss_button),
        )
    }

    if (pendingProfileForActivation.value != null) {
        val profile = pendingProfileForActivation.value
        ConfirmDialog(
            onConfirm = {
                profile?.let { viewModel.toggleProfileActivation(it) }
                pendingProfileForActivation.value = null
            },
            onCancel = { pendingProfileForActivation.value = null },
            title = stringResource(
                R.string.profiles_dialog_activate_title,
                profile?.name.orEmpty()
            ),
            text = stringResource(
                R.string.profiles_dialog_activate_text,
                profile?.durationMillis?.millisToTimeString(context).orEmpty(),
            ),
            confirmButtonText = stringResource(R.string.profiles_button_activate),
            cancelButtonText = stringResource(R.string.profiles_button_cancel),
        )
    }
}

@Composable
internal fun ProfileListScreenContent(
    inactiveProfiles: List<ProfileUi>,
    activeProfiles: List<ProfileUi>,
    isManualProfileActive: Boolean,
    missingPermissions: List<RequiredPermission>,
    formattedTimeRemaining: String,
    onAction: (ProfileListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.profiles_app_title),
        titleFontWeight = FontWeight.ExtraBold,
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { onAction(ProfileListAction.CreateClick) },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.profiles_content_description_add),
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onAction(ProfileListAction.SchedulerClick) },
            ) {
                Icon(
                    painter = painterResource(com.howlindev.appblocker.core.R.drawable.outline_calendar),
                    contentDescription = "Scheduler",
                )
            }
            IconButton(
                onClick = { onAction(ProfileListAction.SettingsClick) },
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.profiles_content_description_settings),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 450.dp),
                contentPadding = PaddingValues(bottom = 140.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (missingPermissions.isNotEmpty()) {
                    item {
                        PermissionListContainerCard(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            missingPermissions = missingPermissions,
                            onShowInfoClick = { onAction(ProfileListAction.ShowPermissionInfo(it)) },
                            onGrantClick = { onAction(ProfileListAction.GrantPermission(it)) },
                        )
                    }
                }

                items(items = activeProfiles, key = { it.id }) { activeProfile ->
                    ActiveProfileListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateItem(),
                        profile = activeProfile,
                        onUnblockClick = {
                            onAction(
                                ProfileListAction.ToggleProfileActivation(
                                    activeProfile,
                                ),
                            )
                        },
                        formattedTimeRemaining = formattedTimeRemaining,
                    )
                }
                items(items = inactiveProfiles, key = { it.id }) { inactiveProfile ->
                    ProfileListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateItem(),
                        profile = inactiveProfile,
                        onClick = { onAction(ProfileListAction.ProfileClick(inactiveProfile.id)) },
                        onToggleProfileActivation = {
                            onAction(ProfileListAction.ToggleProfileActivation(inactiveProfile))
                        },
                        isAnotherProfileActive = isManualProfileActive,
                        onTimerChanged = { newTime ->
                            onAction(
                                ProfileListAction.TimerChange(
                                    profileUi = inactiveProfile,
                                    newTime = newTime,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProfileListScreenPreview() {
    Surface {
        ProfileListScreenContent(
            inactiveProfiles = emptyList(),
            onAction = {},
            activeProfiles = emptyList(),
            isManualProfileActive = false,
            formattedTimeRemaining = "",
            missingPermissions = emptyList(),
        )
    }
}
