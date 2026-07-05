package com.howlindev.appblocker.presentation.block

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.howlindev.appblocker.R
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.util.millisToTimerString
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
    val context = LocalContext.current

    var wasTimed by remember { mutableStateOf(false) }
    if (state.activeBlock?.isTimed == true) {
        wasTimed = true
    }

    val formattedTime = remember(remainingMillis) {
        remainingMillis.millisToTimerString(context)
    }

    BlockScreenContent(
        formattedTimeRemaining = formattedTime,
        modifier = modifier,
        isTimed = state.activeBlock?.isTimed ?: wasTimed,
        blockedApp = state.blockedApp,
        blockedWebsite = state.blockedWebsite,
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
    blockedWebsite: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (blockedWebsite != null) {
            WebsiteBlockHeader()
        } else {
            AppBlockHeader(blockedApp)
        }

        val message = if (blockedWebsite != null) {
            "The webpage $blockedWebsite was blocked because it's in your block list"
        } else {
            stringResource(R.string.block_screen_message, blockedApp?.name.orEmpty())
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )

        if (isTimed) {
            Text(
                text = stringResource(R.string.block_screen_time_left_label),
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
private fun WebsiteBlockHeader() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(com.howlindev.appblocker.core.R.drawable.outline_globe),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Icon(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = CircleShape,
                )
                .padding(4.dp),
            painter = painterResource(com.howlindev.appblocker.core.R.drawable.baseline_lock),
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            contentDescription = null,
        )
    }
}

@Composable
private fun AppBlockHeader(blockedApp: AppInfo?) {
    blockedApp?.let { app ->
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(80.dp),
        ) {
            Image(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                painter = rememberDrawablePainter(app.icon),
                contentDescription = null,
            )
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shape = CircleShape,
                    )
                    .padding(4.dp),
                painter = painterResource(com.howlindev.appblocker.core.R.drawable.baseline_lock),
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                contentDescription = stringResource(
                    com.howlindev.appblocker.core.R.string.lock_icon_content_description,
                ),
            )
        }
    }
}

@Composable
@Preview
fun BlockScreenPreview() {
    Surface {
        BlockScreenContent(
            onAction = {},
            formattedTimeRemaining = "00:05",
            isTimed = true,
            blockedApp = null,
            blockedWebsite = "facebook.com",
        )
    }
}
