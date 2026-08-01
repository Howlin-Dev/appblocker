package com.howlindev.appblocker.profiles.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.presentation.component.ConfirmDialog
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.common.ProfileAppIconGrid
import com.howlindev.appblocker.profiles.presentation.detail.component.RenameProfileDialog
import com.howlindev.appblocker.profiles.presentation.list.component.ProfileDetailAction
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.presentation.ScheduleDialog
import com.howlindev.appblocker.schedule.presentation.ScheduledBlockingItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileDetailScreen(
    profileId: Long,
    onBackClick: () -> Unit,
    onManageAppListClick: (Long) -> Unit,
    onManageWebsiteListClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fallbackTitle = stringResource(R.string.profiles_fallback_title)

    ProfileDetailScreenContent(
        modifier = modifier,
        profile = state.profile,
        isProfileActive = state.isProfileActive,
        onAction = { action ->
            when (action) {
                ProfileDetailAction.BackClick -> onBackClick()
                ProfileDetailAction.DeleteProfile -> viewModel.deleteProfile()
                ProfileDetailAction.ManageAppListClick -> onManageAppListClick(profileId)
                ProfileDetailAction.ManageWebsiteListClick -> onManageWebsiteListClick(profileId)
                is ProfileDetailAction.ProfileNameChanged -> viewModel.updateProfileName(action.name)
                is ProfileDetailAction.DeleteScheduleEvent -> viewModel.deleteScheduleEvent(action.event)
                is ProfileDetailAction.SaveScheduleEvent -> {
                    viewModel.saveScheduleEvent(
                        ScheduleEvent(
                            id = action.eventId,
                            profileId = profileId,
                            startTime = action.from,
                            endTime = action.until,
                            daysOfWeek = action.days,
                            title = state.profile?.name ?: fallbackTitle,
                        ),
                    )
                }
                is ProfileDetailAction.DuplicateProfileConfirmed -> viewModel.duplicateProfile(action.name)
            }
        },
        scheduleEvents = state.scheduleEvents,
    )

    LaunchedEffect(Unit) {
        viewModel.getProfile(profileId)
    }
    LaunchedEffect(state.isProfileDeleted, state.isProfileDuplicated) {
        if (state.isProfileDeleted || state.isProfileDuplicated) {
            onBackClick()
        }
    }
}

@Composable
private fun ProfileDetailScreenContent(
    profile: ProfileUi?,
    scheduleEvents: List<ScheduleEvent>,
    isProfileActive: Boolean,
    onAction: (ProfileDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val renameDialogShown = remember { mutableStateOf(false) }
    val duplicateDialogShown = remember { mutableStateOf(false) }
    val deleteConfirmDialogShown = remember { mutableStateOf(false) }
    var scheduleToEdit by remember { mutableStateOf<ScheduleEvent?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }

    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.profiles_detail_title),
        onBackClick = { onAction(ProfileDetailAction.BackClick) },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.profiles_content_description_more_options),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.profiles_button_duplicate)) },
                    onClick = {
                        expanded = false
                        duplicateDialogShown.value = true
                    },
                )
                DropdownMenuItem(
                    enabled = !isProfileActive,
                    text = { Text(stringResource(R.string.profiles_button_delete)) },
                    onClick = {
                        expanded = false
                        deleteConfirmDialogShown.value = true
                    },
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
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
            ) {
                item {
                    ProfileNameSection(
                        name = profile?.name.orEmpty(),
                        isEditable = !isProfileActive,
                        onRenameClick = { renameDialogShown.value = true },
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
                item {
                    ProfileAppListSection(
                        appList = profile?.blockedApps.orEmpty(),
                        isEnabled = !isProfileActive,
                        onAction = onAction,
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
                item {
                    ProfileWebsiteListSection(
                        websites = profile?.blockedWebsites.orEmpty(),
                        isEnabled = !isProfileActive,
                        onAction = onAction,
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
                profileScheduleSection(
                    scheduleEvents = scheduleEvents,
                    onAddScheduleClick = {
                        scheduleToEdit = null
                        showScheduleDialog = true
                    },
                    onEditScheduleClick = {
                        scheduleToEdit = it
                        showScheduleDialog = true
                    },
                    onDeleteScheduleClick = {
                        onAction(ProfileDetailAction.DeleteScheduleEvent(it))
                    },
                )
            }
        }
    }

    if (showScheduleDialog) {
        ScheduleDialog(
            onDismiss = { showScheduleDialog = false },
            onConfirm = { from, until, days ->
                onAction(
                    ProfileDetailAction.SaveScheduleEvent(
                        from = from,
                        until = until,
                        days = days,
                        eventId = scheduleToEdit?.id ?: 0L,
                    ),
                )
                showScheduleDialog = false
            },
            initialFrom = scheduleToEdit?.startTime ?: java.time.LocalTime.of(9, 0),
            initialUntil = scheduleToEdit?.endTime ?: java.time.LocalTime.of(17, 0),
            initialDays = scheduleToEdit?.daysOfWeek ?: emptySet(),
        )
    }

    if (deleteConfirmDialogShown.value) {
        ConfirmDialog(
            onConfirm = {
                onAction(ProfileDetailAction.DeleteProfile)
                deleteConfirmDialogShown.value = false
            },
            onCancel = { deleteConfirmDialogShown.value = false },
            title = stringResource(R.string.profiles_dialog_delete_title),
            text = stringResource(R.string.profiles_dialog_delete_text),
            confirmButtonText = stringResource(R.string.profiles_button_delete),
            cancelButtonText = stringResource(R.string.profiles_button_cancel),
        )
    }
    if (renameDialogShown.value) {
        RenameProfileDialog(
            name = profile?.name.orEmpty(),
            onChange = {
                onAction(ProfileDetailAction.ProfileNameChanged(it))
                renameDialogShown.value = false
            },
            onCancel = { renameDialogShown.value = false },
        )
    }
    if (duplicateDialogShown.value) {
        RenameProfileDialog(
            title = stringResource(R.string.profiles_dialog_duplicate_title),
            confirmButtonText = stringResource(R.string.profiles_button_duplicate),
            name = profile?.name.orEmpty(),
            onChange = {
                onAction(ProfileDetailAction.DuplicateProfileConfirmed(it))
                duplicateDialogShown.value = false
            },
            onCancel = { duplicateDialogShown.value = false },
        )
    }
}

@Composable
private fun ProfileNameSection(
    name: String,
    isEditable: Boolean,
    onRenameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.profiles_name_section_label),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(
                onClick = onRenameClick,
                enabled = isEditable,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.profiles_content_description_edit),
                )
            }
        }
    }
}

@Composable
private fun ProfileAppListSection(
    appList: List<AppInfo>,
    isEnabled: Boolean,
    onAction: (ProfileDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.profiles_apps_section_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            ProfileAppIconGrid(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                appList = appList,
            )
            TextButton(
                onClick = { onAction(ProfileDetailAction.ManageAppListClick) },
                enabled = isEnabled,
            ) {
                Text(stringResource(R.string.profiles_manage_app_list_button))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.profiles_content_description_manage_list),
                )
            }
        }
    }
}

@Composable
private fun ProfileWebsiteListSection(
    websites: List<String>,
    isEnabled: Boolean,
    onAction: (ProfileDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.profiles_websites_section_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (websites.isEmpty()) {
                    Text(
                        text = stringResource(R.string.profiles_no_websites_blocked),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        websites.forEach { website ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                                    text = website,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = { onAction(ProfileDetailAction.ManageWebsiteListClick) },
                enabled = isEnabled,
            ) {
                Text(stringResource(R.string.profiles_manage_website_list_button))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.profiles_content_description_manage_list),
                )
            }
        }
    }
}

private fun LazyListScope.profileScheduleSection(
    scheduleEvents: List<ScheduleEvent>,
    onAddScheduleClick: () -> Unit,
    onEditScheduleClick: (ScheduleEvent) -> Unit,
    onDeleteScheduleClick: (ScheduleEvent) -> Unit,
) {
    item {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.profiles_schedule_section_label),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (scheduleEvents.isEmpty()) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.profiles_no_schedule_active),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        items(
            items = scheduleEvents,
            key = { it.id },
        ) { event ->
            ScheduledBlockingItem(
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 16.dp),
                event = event,
                onEditClick = { onEditScheduleClick(event) },
                onRemoveClick = { onDeleteScheduleClick(event) },
                isEnabled = !event.isActiveNow(),
            )
        }
    }

    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            TextButton(
                onClick = onAddScheduleClick,
            ) {
                Text(stringResource(R.string.profiles_add_schedule_button))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.profiles_content_description_add_schedule),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileDetailScreenPreview() {
    ProfileDetailScreenContent(
        profile = ProfileUi(
            name = "Reading",
        ),
        scheduleEvents = emptyList(),
        isProfileActive = false,
        onAction = { },
    )
}
