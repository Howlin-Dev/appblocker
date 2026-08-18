package com.howlindev.appblocker.permissions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.permissions.R
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission

@Composable
fun PermissionItem(
    permission: RequiredPermission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMalfunctioning = (permission as? RequiredPermission.Accessibility)?.isMalfunctioning == true
    val isRestricted = permission.isRestricted
    val isOnePlus = permission.isOnePlus

    val cardColors = if (isMalfunctioning || isRestricted) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    } else {
        CardDefaults.cardColors()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = cardColors,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = stringResource(permission.titleRes), style = MaterialTheme.typography.titleMedium)

            when {
                isMalfunctioning -> {
                    Text(
                        text = stringResource(R.string.permission_accessibility_malfunctioning_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                isRestricted -> {
                    Text(
                        text = stringResource(R.string.permission_restricted_settings_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                else -> {
                    Text(text = stringResource(permission.subtitleRes), style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            if (isOnePlus && !isRestricted && !isMalfunctioning) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(R.string.permission_oneplus_tip),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = onClick,
                colors = if (isMalfunctioning || isRestricted) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                },
            ) {
                Text(
                    text = stringResource(
                        if (isRestricted) {
                            R.string.permission_restricted_settings_button
                        } else {
                            R.string.permission_grant_button
                        },
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
fun PermissionItemPreview() {
    Surface {
        PermissionItem(
            modifier = Modifier.padding(16.dp),
            permission = RequiredPermission.Overlay(restricted = false),
            onClick = { },
        )
    }
}
