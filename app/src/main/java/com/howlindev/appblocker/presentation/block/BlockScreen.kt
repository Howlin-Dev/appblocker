package com.howlindev.appblocker.presentation.block

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.howlindev.appblocker.R
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.util.formatMillis
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
        blockedApp = state.blockedApp,
        onAction = { action ->
            when (action) {
                BlockAction.OnClose -> onClose()
            }
        },
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
    blockedApp: AppInfo?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        blockedApp?.let { app ->
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(80.dp),
            ) {
                Image(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    painter = rememberDrawablePainter(app.icon),
                    contentDescription = null,
                )
                Icon(
                    modifier = Modifier.size(32.dp).align(Alignment.BottomEnd),
                    painter = painterResource(com.howlindev.appblocker.core.R.drawable.baseline_lock),
                    tint = MaterialTheme.colorScheme.inverseSurface,
                    contentDescription = "Lock",
                )
            }
        }
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.block_screen_message, blockedApp?.name.orEmpty()),
            style = MaterialTheme.typography.titleMedium,
        )
        if (isTimed) {
            Text(
                text = "Time left until unblocked:",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = formattedTimeRemaining,
                style = MaterialTheme.typography.displayLarge,
            )
        }
        TextButton(
            onClick = { onAction(BlockAction.OnClose) },
        ) {
            Text(
                text = stringResource(R.string.block_screen_close_button_text),
            )
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

