package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.schedule.R
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SchedulerScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SchedulerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SchedulerScreenContent(
        state = state,
        onBackClick = onBackClick,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun SchedulerScreenContent(
    state: SchedulerState,
    onBackClick: () -> Unit,
    onAction: (SchedulerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayNames = listOf(
        stringResource(R.string.schedule_day_monday),
        stringResource(R.string.schedule_day_tuesday),
        stringResource(R.string.schedule_day_wednesday),
        stringResource(R.string.schedule_day_thursday),
        stringResource(R.string.schedule_day_friday),
        stringResource(R.string.schedule_day_saturday),
        stringResource(R.string.schedule_day_sunday),
    )

    val pagerState = rememberPagerState(pageCount = { 7 })
    val coroutineScope = rememberCoroutineScope()

    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.schedule_app_title),
        onBackClick = onBackClick,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                dayNames.forEachIndexed { index, name ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = name) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { dayIndex ->
                ScheduleTimeline(
                    events = state.eventsByDay[dayIndex] ?: emptyList(),
                    onHourClick = { /* TODO */ },
                    onEventClick = { /* TODO */ }
                )
            }
        }
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
                onAction = {}
            )
        }
    }
}
