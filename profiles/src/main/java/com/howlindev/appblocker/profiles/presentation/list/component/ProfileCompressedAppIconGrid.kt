package com.howlindev.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.howlindev.appblocker.core.domain.model.AppInfo

@Composable
fun ProfileCompressedAppIconGrid(
    appList: List<AppInfo>,
    modifier: Modifier = Modifier,
    columns: Int = 6,
    maxRows: Int = 2,
    spacing: Dp = 8.dp,
) {
    val maxTotalSpots = columns * maxRows
    val showOverflow = appList.size > maxTotalSpots

    val overflowSpan = if (showOverflow) 2 else 0
    val displayCount = if (showOverflow) maxTotalSpots - overflowSpan else appList.size
    val overflowCount = appList.size - displayCount

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val itemSize = (maxWidth - (spacing * (columns - 1))) / columns

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            for (rowIndex in 0 until maxRows) {
                val rowStart = rowIndex * columns
                val isLastRow = rowIndex == maxRows - 1

                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    var columnIndex = 0
                    while (columnIndex < columns) {
                        val itemIndex = rowStart + columnIndex

                        when {
                            itemIndex < displayCount -> {
                                Image(
                                    painter = rememberDrawablePainter(appList[itemIndex].icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(itemSize),
                                )
                                columnIndex++
                            }
                            showOverflow && isLastRow && columnIndex >= columns - overflowSpan -> {
                                val tileWidth = (itemSize * overflowSpan) + (spacing * (overflowSpan - 1))
                                OverflowTile(
                                    count = overflowCount,
                                    width = tileWidth,
                                    height = itemSize,
                                )
                                columnIndex += overflowSpan
                            }
                            else -> {
                                Spacer(modifier = Modifier.size(itemSize))
                                columnIndex++
                            }
                        }
                    }
                }

                if (rowStart + columns >= displayCount && (!showOverflow || !isLastRow)) break
            }
        }
    }
}

@Composable
private fun OverflowTile(
    count: Int,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(width)
            .height(height),
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "+$count",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
