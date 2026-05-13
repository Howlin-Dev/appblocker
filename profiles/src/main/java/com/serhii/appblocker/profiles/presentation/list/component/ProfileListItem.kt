package com.serhii.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serhii.appblocker.core.R
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi

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
            .height(160.dp)
            .padding(horizontal = 16.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if(isActive) MaterialTheme.colorScheme.primary else Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
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
                    .fillMaxHeight()
                    .width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isActive && formattedTimeRemaining.isNotEmpty()) {
                    TimerSection(
                        formattedTimeRemaining = formattedTimeRemaining,
                    )
                } else {
                    ActivateProfileButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        isProfileActive = isActive,
                        isAnotherProfileActive = isAnotherProfileActive,
                        onClick = onToggleProfileActivation,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
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
private fun TimerSection(
    formattedTimeRemaining: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Active for:")
        Text(
            modifier = modifier,
            text = formattedTimeRemaining,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
private fun TimerButton(
    isProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        modifier = modifier
            .padding(0.dp)
            .height(40.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
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
            profile = ProfileUi(0L, "Working Out", "", emptyList(), null),
            onClick = {},
            onToggleProfileActivation = {},
            isActive = false,
            isAnotherProfileActive = true,
            formattedTimeRemaining = "00:00",
        )
    }
}
