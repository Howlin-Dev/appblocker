package com.serhii.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.serhii.appblocker.core.R
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi

@Composable
fun ProfileListItem(
    profile: ProfileUi,
    isActive: Boolean,
    isAnotherProfileActive: Boolean,
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (profile.description.isNotEmpty()) {
                Text(
                    text = profile.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profile.blockedApps.forEach {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = rememberDrawablePainter(it.icon),
                        contentDescription = null
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                ActivateProfileButton(
                    isActive = isActive,
                    isAnotherProfileActive = isAnotherProfileActive,
                    onClick = onToggleProfileActivation,
                )
            }
        }
    }
}

@Composable
private fun ActivateProfileButton(
    isActive: Boolean,
    isAnotherProfileActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = !isAnotherProfileActive,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_lock),
                contentDescription = "Lock"
            )
            Text(
                text = if(isActive) "Deactivate" else "Activate",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
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
            profile = ProfileUi(0L, "", "", emptyList()),
            onClick = {},
            onToggleProfileActivation = {},
            isActive = false,
            isAnotherProfileActive = false,
        )
    }
}
