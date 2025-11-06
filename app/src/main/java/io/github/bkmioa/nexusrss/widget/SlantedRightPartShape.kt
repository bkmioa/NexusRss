package io.github.bkmioa.nexusrss.widget

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * 一个自定义 Shape，用于裁剪出右侧的斜切部分。
 * @param slant 斜线在水平方向上的宽度。
 */
class SlantedRightPartShape(private val slant: Dp, private val radius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val slantPx = with(density) {
            slant.toPx()
        }
        val radiusPx = with(density) {
            radius.toPx()
        }
        val path = Path().apply {
            moveTo(slantPx, 0f)
            lineTo(size.width - radiusPx / 2, 0f)
            arcTo(Rect(size.width - 2 * radiusPx, 0f, size.width, 2 * radiusPx), -90f, 90f, false)
            lineTo(size.width, size.height - radiusPx / 2)
            arcTo(Rect(size.width - 2 * radiusPx, size.height - 2 * radiusPx, size.width, size.height), 0f, 90f, false)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}