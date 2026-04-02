package com.serhii.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.serhii.appblocker.core.R
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import kotlin.math.max
import kotlin.math.min

@Composable
fun ProfileListItem(
    profile: ProfileUi,
    isActive: Boolean,
    isAnotherProfileActive: Boolean,
    formattedTimeRemaining: String,
    onClick: () -> Unit,
    onToggleProfileActivation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AppIconGrid(
                    profileActive = isActive,
                    appList = profile.blockedApps,
                )
            }
            Column(
                modifier = Modifier
                    .width(100.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isActive && formattedTimeRemaining.isNotEmpty()) {
                    Text(formattedTimeRemaining)
                } else {
                    ActivateProfileButton(
                        modifier = Modifier.fillMaxWidth(),
                        isProfileActive = isActive,
                        isAnotherProfileActive = isAnotherProfileActive,
                        onClick = onToggleProfileActivation,
                    )
                    TimerButton(
                        modifier = Modifier.fillMaxWidth(),
                        isProfileActive = isActive,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun AppList(
    profileActive: Boolean,
    appList: List<AppInfo>,
    modifier: Modifier = Modifier,
    maxItems: Int = 12,
) {
    FlowRow(
        modifier = modifier
            .padding(top = 8.dp)
            .alpha(if (profileActive) 0.75f else 1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(min(appList.size, maxItems)) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = rememberDrawablePainter(appList[it].icon),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun TimerButton(
    isProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        onClick = onClick,
        enabled = !isProfileActive,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Timer",
            )
            Icon(
                painter = painterResource(R.drawable.outline_timer),
                contentDescription = "Timer"
            )
        }
    }
}

@Composable
private fun ActivateProfileButton(
    isProfileActive: Boolean,
    isAnotherProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = !isAnotherProfileActive,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_lock),
                contentDescription = "Lock"
            )
            Text(
                text = if (isProfileActive) "Unlock" else "Activate",
            )
        }
    }
}

@Preview
@Composable
private fun ProfileListItemPreview() {
    Surface {
        ProfileListItem(
            modifier = Modifier.padding(16.dp),
            profile = ProfileUi(0L, "Working Out", "", emptyList()),
            onClick = {},
            onToggleProfileActivation = {},
            isActive = false,
            isAnotherProfileActive = false,
            formattedTimeRemaining = "",
        )
    }
}
