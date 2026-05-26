package com.serhii.appblocker.profiles.presentation.list.component

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serhii.appblocker.core.R
import com.serhii.appblocker.core.util.millisToTimeString
import com.serhii.appblocker.profiles.presentation.common.TimerPickerDialog
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.R as ProfilesR

@Composable
fun ProfileListItem(
    profile: ProfileUi,
    isAnotherProfileActive: Boolean,
    onClick: () -> Unit,
    onTimerChanged: (Long?) -> Unit,
    onToggleProfileActivation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTimerDialogShown = remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                ProfileCompressedAppIconGrid(
                    appList = profile.blockedApps,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(115.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ActivateProfileButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isAnotherProfileActive = isAnotherProfileActive,
                    onClick = onToggleProfileActivation,
                )
                Spacer(modifier = Modifier.size(8.dp))
                TimerButton(
                    modifier = Modifier.fillMaxWidth(),
                    time = profile.durationMillis,
                    onClick = { isTimerDialogShown.value = true },
                )
            }
        }
    }

    if (isTimerDialogShown.value) {
        TimerPickerDialog(
            time = profile.durationMillis,
            onConfirm = {
                onTimerChanged(it)
                isTimerDialogShown.value = false
            },
            onCancel = { isTimerDialogShown.value = false },
        )
    }
}

@Composable
private fun TimerButton(
    time: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        modifier = modifier
            .padding(0.dp)
            .height(40.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = time?.millisToTimeString() ?: stringResource(ProfilesR.string.profiles_timer_label),
            )
            Icon(
                painter = painterResource(R.drawable.outline_timer),
                contentDescription = stringResource(ProfilesR.string.profiles_timer_label),
            )
        }
    }
}

@Composable
private fun ActivateProfileButton(
    isAnotherProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = !isAnotherProfileActive,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_lock),
                contentDescription = stringResource(ProfilesR.string.profiles_content_description_lock),
            )
            Text(
                stringResource(ProfilesR.string.profiles_button_activate),
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
            onTimerChanged = {},
            onToggleProfileActivation = {},
            isAnotherProfileActive = true,
        )
    }
}
