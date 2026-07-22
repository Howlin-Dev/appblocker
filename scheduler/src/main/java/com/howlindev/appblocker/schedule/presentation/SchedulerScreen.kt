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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.schedule.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

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
        modifier = modifier,
    )
}

@Composable
fun SchedulerScreenContent(
    state: SchedulerState,
    onBackClick: () -> Unit,
    onAction: (SchedulerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = remember { DayOfWeek.entries }
    val pagerState = rememberPagerState(pageCount = { days.size })
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
                            Text(text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()))
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
                    onHourClick = { /* TODO */ },
                    onEventClick = { /* TODO */ },
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
                onAction = {},
            )
        }
    }
}
