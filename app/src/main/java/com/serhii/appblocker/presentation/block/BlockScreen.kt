package com.serhii.appblocker.presentation.block

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.util.formatMillis
import org.koin.androidx.compose.koinViewModel

@Composable
fun BlockScreen(
    onClose: () -> Unit,
    onTimerRunsOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BlockViewModel = koinViewModel(),
) {
    val remainingMillis by viewModel.remainingTime.collectAsState()
    val state by viewModel.state.collectAsState()

    // Remember if this session was timed, to avoid missing the finish event
    // when activeBlock becomes null at the same time the timer reaches zero.
    var wasTimed by remember { mutableStateOf(false) }
    if (state.activeBlock?.isTimed == true) {
        wasTimed = true
    }

    val formattedTime = remember(remainingMillis) {
        formatMillis(remainingMillis)
    }

    BlockScreenContent(
        formattedTimeRemaining = formattedTime,
        modifier = modifier,
        isTimed = state.activeBlock?.isTimed ?: wasTimed,
        onAction = { action ->
            when (action) {
                BlockAction.OnClose -> onClose()
            }
        }
    )

    LaunchedEffect(remainingMillis) {
        if (remainingMillis <= 0 && wasTimed) {
            onTimerRunsOut()
        }
    }
}


@Composable
private fun BlockScreenContent(
    onAction: (BlockAction) -> Unit,
    formattedTimeRemaining: String,
    isTimed: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "This app is currently blocked",
            style = MaterialTheme.typography.titleLarge,
        )
        if (isTimed) {
            Text(
                text = formattedTimeRemaining,
                style = MaterialTheme.typography.displayLarge,
            )
        }
        TextButton(
            onClick = { onAction(BlockAction.OnClose) }
        ) {
            Text("Close")
        }
    }
}


@Composable
@Preview
fun BlockScreenPreview() {
    Surface {
        BlockScreen(
            onClose = { },
            onTimerRunsOut = { },
        )
    }
}