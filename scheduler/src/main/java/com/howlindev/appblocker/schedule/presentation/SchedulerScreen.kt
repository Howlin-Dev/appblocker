package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.schedule.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SchedulerScreen(
    onBackClick: () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SchedulerViewModel = koinViewModel(),
    newProfileId: Long? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(newProfileId) {
        if (newProfileId != null) {
            viewModel.onAction(SchedulerAction.ProfileCreated(newProfileId))
        }
    }

    SchedulerScreenContent(
        state = state,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onCreateProfileClick = onCreateProfileClick,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun SchedulerScreenContent(
    state: SchedulerState,
    onBackClick: () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateProfileClick: () -> Unit,
    onAction: (SchedulerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = remember { DayOfWeek.entries }
    val currentDay = remember { LocalDate.now().dayOfWeek }
    val initialPage = remember(days, currentDay) { days.indexOf(currentDay).coerceAtLeast(0) }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { days.size },
    )
    val coroutineScope = rememberCoroutineScope()

    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.schedule_app_title),
        onBackClick = onBackClick,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
            ) {
                days.forEachIndexed { index, day ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                                fontWeight = if (day == currentDay) FontWeight.ExtraBold else FontWeight.Normal,
                            )
                        },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { pageIndex ->
                val day = days[pageIndex]
                ScheduleTimeline(
                    events = state.eventsByDay[day] ?: emptyList(),
                    dayOfWeek = day,
                    onHourClick = { onAction(SchedulerAction.ShowScheduleDialog(it, day)) },
                    onEventClick = { onProfileClick(it.profileId) },
                )
            }
        }
    }

    if (state.isScheduleDialogOpen && state.draftSchedule != null) {
        ScheduleDialog(
            title = stringResource(R.string.schedule_dialog_create_title),
            onDismiss = { onAction(SchedulerAction.DismissScheduleDialog) },
            onConfirm = { from, until, days ->
                onAction(SchedulerAction.ConfirmSchedule(from, until, days))
            },
            initialFrom = state.draftSchedule.from,
            initialUntil = state.draftSchedule.until,
            initialDays = state.draftSchedule.days,
            profiles = state.profiles,
            selectedProfileId = state.selectedProfileId,
            onProfileSelect = { onAction(SchedulerAction.ProfileSelected(it)) },
            onCreateProfileClick = onCreateProfileClick,
        )
    }
}

@Preview
@Composable
private fun SchedulerScreenPreview() {
    MaterialTheme {
        Surface {
            SchedulerScreenContent(
                state = SchedulerState(),
                onBackClick = {},
                onProfileClick = {},
                onCreateProfileClick = {},
                onAction = {},
            )
        }
    }
}
