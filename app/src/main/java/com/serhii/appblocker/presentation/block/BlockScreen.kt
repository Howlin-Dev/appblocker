package com.serhii.appblocker.presentation.block

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
import androidx.compose.runtime.remember
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

    val formattedTime = remember(remainingMillis) {
        formatMillis(remainingMillis)
    }

    BlockScreenContent(
        formattedTimeRemaining = formattedTime,
        modifier = modifier,
        onAction = { action ->
            when(action) {
                BlockAction.OnClose -> onClose()
            }
        }
    )

    LaunchedEffect(remainingMillis) {
        if(remainingMillis <= 0) {
            onTimerRunsOut()
        }
    }
}

@Composable
private fun BlockScreenContent(
    onAction: (BlockAction) -> Unit,
    formattedTimeRemaining: String,
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
        Text(
            text = formattedTimeRemaining,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 120.sp
        )
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