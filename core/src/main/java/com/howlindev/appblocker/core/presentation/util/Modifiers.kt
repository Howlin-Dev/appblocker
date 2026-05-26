package com.howlindev.appblocker.core.presentation.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimmer")

    val width = if (size.width > 0) size.width.toFloat() else 1000f

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * width,
        targetValue = 2 * width,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
        ),
        label = "shimmer",
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Gray.copy(alpha = 0.2f),
                Color.Gray.copy(alpha = 0.4f),
                Color.Gray.copy(alpha = 0.2f),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + width, width),
        ),
    ).onGloballyPositioned {
        size = it.size
    }
}

