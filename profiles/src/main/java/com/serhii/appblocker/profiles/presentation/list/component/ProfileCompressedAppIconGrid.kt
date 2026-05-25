package com.serhii.appblocker.profiles.presentation.list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.serhii.appblocker.core.domain.model.AppInfo

@Composable
fun ProfileCompressedAppIconGrid(
    appList: List<AppInfo>,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp
) {
    val firstRow = appList.take(6)
    val hasOverflow = appList.size > 12
    val secondRowRaw = appList.drop(6).take(6)

    val secondRow = if (hasOverflow) {
        secondRowRaw.take(4) // leave space for "+n" tile
    } else {
        secondRowRaw
    }

    val overflowCount = (appList.size - (12 - 2)).coerceAtLeast(0)

    BoxWithConstraints(modifier) {
        val totalSpacing = spacing * 5
        val itemWidth = (maxWidth - totalSpacing) / 6

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {

            // First row
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                firstRow.forEach {
                    Image(
                        modifier = Modifier.size(itemWidth),
                        painter = rememberDrawablePainter(it.icon),
                        contentDescription = null
                    )
                }
            }

            // Second row
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {

                secondRow.forEach {
                    Image(
                        modifier = Modifier.size(itemWidth),
                        painter = rememberDrawablePainter(it.icon),
                        contentDescription = null
                    )
                }

                if (hasOverflow) {
                    // "+n" tile spanning 2 columns
                    Surface(
                        shape = RoundedCornerShape(itemWidth / 2),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Box(
                            modifier = Modifier
                                .width(itemWidth * 2 + spacing)
                                .height(itemWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$overflowCount",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                } else {
                    // fill remaining slots (optional, keeps alignment)
                    repeat(6 - secondRow.size) {
                        Spacer(
                            modifier = Modifier
                                .width(itemWidth)
                                .height(itemWidth)
                        )
                    }
                }
            }
        }
    }
}