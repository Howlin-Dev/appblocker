package com.howlindev.appblocker.permissions.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.permissions.R
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission

@Composable
fun PermissionListContainerCard(
    missingPermissions: List<RequiredPermission>,
    onShowInfoClick: (RequiredPermission) -> Unit,
    onGrantClick: (RequiredPermission) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                text = stringResource(R.string.permissions_screen_description),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Surface(shape = RoundedCornerShape(8.dp)) {
                Column {
                    missingPermissions.forEachIndexed { idx, permission ->
                        PermissionListContainerItem(
                            title = stringResource(permission.titleRes),
                            onShowInfoClick = { onShowInfoClick(permission) },
                            onGrantClick = { onGrantClick(permission) },
                        )
                        if (idx < missingPermissions.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionListContainerItem(
    modifier: Modifier = Modifier,
    title: String,
    onShowInfoClick: () -> Unit,
    onGrantClick: () -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
        IconButton(
            onClick = onShowInfoClick,
        ) {
            Icon(
                painter = painterResource(com.howlindev.appblocker.core.R.drawable.outline_info),
                contentDescription = null,
            )
        }
        Button(
            onClick = onGrantClick,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(stringResource(R.string.permission_grant_button))
        }
    }
}

@Preview
@Composable
private fun PermissionListContainerItemPreview() {
    PermissionListContainerCard(
        missingPermissions = listOf(
            RequiredPermission.Overlay(restricted = false),
            RequiredPermission.UsageAccess(restricted = false),
        ),
        onShowInfoClick = {},
        onGrantClick = {},
    )
}
