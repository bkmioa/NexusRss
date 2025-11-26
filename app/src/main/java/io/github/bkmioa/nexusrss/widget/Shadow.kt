package io.github.bkmioa.nexusrss.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.hazeEffect

@Composable
fun Shadow(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 4.dp,
    spread: Dp = 2.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
    color: Color = Color.Black.copy(alpha = 0.6f),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .graphicsLayer {
                    if (size.width > 0 && size.height > 0) {
                        val pxSpread = spread.toPx()
                        scaleX = (size.width + pxSpread * 2) / size.width
                        scaleY = (size.height + pxSpread * 2) / size.height
                    }
                }
                .hazeEffect {
                    this.blurRadius = blurRadius
                }
                .forceShadowColor(color)
        ) {
            content()
        }

        content()
    }
}

fun Modifier.forceShadowColor(color: Color): Modifier = this.drawWithContent {
    val shadowColorFilter = ColorFilter.tint(color, BlendMode.SrcIn)
    val paint = Paint().apply {
        colorFilter = shadowColorFilter
    }

    drawIntoCanvas { canvas ->
        canvas.withSaveLayer(size.toRect(), paint) {
            this@drawWithContent.drawContent()
        }
    }
}