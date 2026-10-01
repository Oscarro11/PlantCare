package plat.plantcare.app.core

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp

fun darkenColor(originalColor: Color, factor: Float = 0.1f): Color {
    return Color.Black.copy(alpha = factor).compositeOver(background = originalColor)
}

fun lightenColor(originalColor: Color, factor: Float = 0.1f): Color {
    return originalColor.copy(alpha = 1 - factor).compositeOver(background = Color.White)
}

fun BoxWithConstraintsScope.relativePadding(
    factor: Float,
    minimum: Dp,
    maximum: Dp
): Dp {
    require(factor >= 0f) {
        "The padding factor cannot be negative."
    }

    require(minimum <= maximum) {
        "Minimum padding cannot exceed maximum padding."
    }

    return (maxWidth * factor).coerceIn(
        minimumValue = minimum,
        maximumValue = maximum
    )
}