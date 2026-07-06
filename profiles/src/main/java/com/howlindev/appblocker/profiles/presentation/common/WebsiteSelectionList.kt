package com.howlindev.appblocker.profiles.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.profiles.R

@Composable
fun WebsiteSelectionList(
    suggestedWebsites: List<String>,
    selectedWebsites: Set<String>,
    onWebsiteSelected: (String) -> Unit,
    onCustomWebsiteAdded: (String) -> Unit,
    onWebsiteRemoved: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var websiteInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = websiteInput,
                onValueChange = { websiteInput = it },
                label = { Text(stringResource(R.string.profiles_add_website_label)) },
                singleLine = true,
            )
            IconButton(
                onClick = {
                    if (websiteInput.isNotBlank()) {
                        onCustomWebsiteAdded(websiteInput.trim())
                        websiteInput = ""
                    }
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.profiles_content_description_add_website))
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(suggestedWebsites) { website ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { onWebsiteSelected(website) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Checkbox(
                            checked = selectedWebsites.contains(website),
                            onCheckedChange = { onWebsiteSelected(website) },
                        )
                        Text(text = website, modifier = Modifier.padding(start = 8.dp))
                    }

                    IconButton(onClick = { onWebsiteRemoved(website) }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.profiles_content_description_remove_website))
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
