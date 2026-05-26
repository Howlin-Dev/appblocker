package com.howlindev.appblocker.profiles.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.howlindev.appblocker.core.domain.model.AppInfo

@Composable
fun ProfileAppIconGrid(
    appList: List<AppInfo>,
    modifier: Modifier = Modifier,
    columns: Int = 8,
    itemSpacing: Dp = 6.dp,
) {
    val rows = appList.chunked(columns)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            ) {
                rowItems.forEach { appInfo ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    ) {
                        Image(
                            painter = rememberDrawablePainter(appInfo.icon),
                            contentDescription = null,
                        )
                    }
                }

                repeat(columns - rowItems.size) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
            }
        }
    }
}

