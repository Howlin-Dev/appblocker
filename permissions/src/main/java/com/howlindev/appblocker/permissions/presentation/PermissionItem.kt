package com.howlindev.appblocker.permissions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.permissions.R

@Composable
fun PermissionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            Button(
                modifier = Modifier.padding(top = 4.dp),
                onClick = onClick,
            ) {
                Text(stringResource(R.string.permission_grant_button))
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
            title = "Accessibility Functionality",
            subtitle = "We need many accessibility permissions for you app to work please " +
                "give give the permission now please",
            onClick = { },
        )
    }
}

