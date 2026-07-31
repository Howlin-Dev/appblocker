package com.howlindev.appblocker.core.presentation.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

val LocalShimmerTransition = compositionLocalOf<InfiniteTransition?> { null }

fun Modifier.shimmerEffect(
    shimmerColors: List<Color> = listOf(
        Color.Gray.copy(alpha = 0.2f),
        Color.Gray.copy(alpha = 0.4f),
        Color.Gray.copy(alpha = 0.2f),
    ),
    shape: Shape = RectangleShape,
    infiniteTransition: InfiniteTransition? = null
): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    
    val transition = infiniteTransition 
        ?: LocalShimmerTransition.current 
        ?: rememberInfiniteTransition(label = "shimmer")

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
            colors = shimmerColors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + width, width),
        ),
        shape = shape
    ).onGloballyPositioned {
        size = it.size
    }
}
