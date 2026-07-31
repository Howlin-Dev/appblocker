package com.howlindev.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.core.presentation.util.shimmerEffect
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.common.ProfileAppIconGrid
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi

@Composable
fun ActiveProfileListItem(
    profile: ProfileUi,
    onUnblockClick: () -> Unit,
    formattedTimeRemaining: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    onAccentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    shimmerColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val shape = RoundedCornerShape(12.dp)
    val innerShape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(accentColor, shape)
            .shimmerEffect(
                shimmerColors = listOf(
                    shimmerColor.copy(alpha = 0f),
                    shimmerColor.copy(alpha = 0.2f),
                    shimmerColor.copy(alpha = 0f),
                ),
                shape = shape
            )
            .clip(shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = profile.name,
                style = MaterialTheme.typography.titleLarge,
                color = onAccentColor,
                fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            )
            if (!profile.isManuallyActive && profile.scheduledEndTime != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = stringResource(R.string.profiles_active_until),
                        style = MaterialTheme.typography.labelMedium,
                        color = onAccentColor.copy(alpha = 0.8f),
                    )
                    Text(
                        text = profile.scheduledEndTime,
                        style = MaterialTheme.typography.headlineSmall,
                        color = onAccentColor,
                        fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
                    )
                }
            } else if (profile.durationMillis == null) {
                Button(
                    onClick = onUnblockClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = onAccentColor,
                        contentColor = accentColor,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(stringResource(R.string.profiles_unblock_button))
                        Icon(
                            painter = painterResource(com.howlindev.appblocker.core.R.drawable.outline_lock_open),
                            contentDescription = stringResource(R.string.profiles_content_description_lock),
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = stringResource(R.string.profiles_active_for),
                        style = MaterialTheme.typography.labelMedium,
                        color = onAccentColor.copy(alpha = 0.8f),
                    )
                    Text(
                        text = formattedTimeRemaining,
                        style = MaterialTheme.typography.headlineSmall,
                        color = onAccentColor,
                        fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = innerShape,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (profile.blockedApps.isNotEmpty()) {
                    ProfileAppIconGrid(
                        appList = profile.blockedApps,
                    )
                }

                if (profile.blockedWebsites.isNotEmpty()) {
                    FlowRow(
//                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        profile.blockedWebsites.forEach { website ->
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
        }
    }
}

@Preview
@Composable
fun ActiveProfileListItemPreview() {
    Surface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActiveProfileListItem(
                profile = ProfileUi(
                    name = "Reading while sitting in auiohpsdj",
                    blockedApps = emptyList(),
                    blockedWebsites = listOf("facebook.com", "instagram.com")
                ),
                onUnblockClick = { },
                formattedTimeRemaining = "",
            )
            ActiveProfileListItem(
                profile = ProfileUi(
                    name = "Reading",
                    durationMillis = 100,
                    blockedApps = emptyList(),
                    blockedWebsites = emptyList()
                ),
                onUnblockClick = { },
                formattedTimeRemaining = "20:50",
            )
            ActiveProfileListItem(
                profile = ProfileUi(
                    name = "Reading",
                    isManuallyActive = false,
                    scheduledEndTime = "12:50",
                    blockedApps = emptyList(),
                    blockedWebsites = listOf("youtube.com")
                ),
                onUnblockClick = { },
                formattedTimeRemaining = "20:50",
            )
        }
    }
}
