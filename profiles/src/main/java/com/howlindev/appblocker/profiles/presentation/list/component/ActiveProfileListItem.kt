package com.howlindev.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.common.ProfileAppIconGrid
import com.howlindev.appblocker.profiles.presentation.list.model.ProfileUi

@Composable
fun ActiveProfileListItem(
    profile: ProfileUi,
    onUnblockClick: () -> Unit,
    formattedTimeRemaining: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = profile.name,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.size(8.dp))
        if (profile.durationMillis == null) {
            Button(
                modifier = Modifier
                    .height(64.dp)
                    .padding(8.dp),
                onClick = onUnblockClick,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(stringResource(R.string.profiles_unblock_button))
            }
        } else {
            Text(
                text = stringResource(R.string.profiles_active_for),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = formattedTimeRemaining,
                style = MaterialTheme.typography.displayLarge,
            )
        }
        if (profile.blockedApps.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.profiles_blocked_apps_label),
                style = MaterialTheme.typography.labelMedium,
            )
            ProfileAppIconGrid(
                appList = profile.blockedApps,
            )
        }

        if (profile.blockedWebsites.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.profiles_blocked_websites_label),
                style = MaterialTheme.typography.labelMedium,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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
        Spacer(modifier = Modifier.padding(8.dp))
    }
}

@Preview
@Composable
fun ActiveProfileListItemPreview() {
    Surface {
        ActiveProfileListItem(
            profile = ProfileUi(
                name = "Reading",
            ),
            onUnblockClick = { },
            formattedTimeRemaining = "",
        )
    }
}
