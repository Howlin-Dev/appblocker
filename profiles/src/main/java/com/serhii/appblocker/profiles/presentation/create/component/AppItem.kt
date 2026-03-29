package com.serhii.appblocker.profiles.presentation.create.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppItem(
    appName: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = appName)
        }
    }
}

@Preview
@Composable
private fun AppItemPreview() {
    Surface {
        AppItem(
            appName = "Twitter",
            selected = false,
        )
    }
}