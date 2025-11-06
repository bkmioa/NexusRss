package io.github.bkmioa.nexusrss.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.model.Item


@Composable
fun Labels(modifier: Modifier = Modifier, item: Item, hazeState: HazeState, alignEndSeederAndLeecher: Boolean = false) {
    FlowRow(
        modifier = modifier
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        @Composable
        fun seederAndLeecher() {
            Row {
                LabelBox(
                    hazeState,
                    shape = MaterialTheme.shapes.small.copy(topEnd = ZeroCornerSize, bottomEnd = ZeroCornerSize),
                    backgroundColor = colorResource(R.color.label_bg_up),
                    contentColor = colorResource(R.color.label_fg_up)
                ) {
                    appendInlineContent("icon_seeder")
                    append(item.status.seeders)
                }
                LabelBox(
                    hazeState,
                    shape = MaterialTheme.shapes.small.copy(topStart = ZeroCornerSize, bottomStart = ZeroCornerSize),
                    backgroundColor = colorResource(R.color.label_bg_down),
                    contentColor = colorResource(R.color.label_fg_down)
                ) {
                    appendInlineContent("icon_leecher")
                    append(item.status.leechers)
                }
            }
        }
        if (!alignEndSeederAndLeecher) {
            seederAndLeecher()
        }


        item.labels?.forEach { label ->
            val color = when {
                label.contains("4k", true) -> colorResource(R.color.label_bg_4k) to colorResource(R.color.label_fg_4k)
                label.contains("8k", true) -> colorResource(R.color.label_bg_8k) to colorResource(R.color.label_fg_8k)
                label.contains("hdr", true) -> colorResource(R.color.label_bg_hdr) to colorResource(R.color.label_fg_hdr)
                label.contains("dovi", true) -> colorResource(R.color.label_bg_dovi) to colorResource(R.color.label_fg_dovi)
                label.contains("hlg", true) -> colorResource(R.color.label_bg_4k) to colorResource(R.color.label_fg_4k)
                else -> colorResource(R.color.label_bg_default) to colorResource(R.color.label_fg_default)
            }
            LabelBox(hazeState, color.first, color.second, label.uppercase())
        }
        if (alignEndSeederAndLeecher) {
            seederAndLeecher()
        }
    }
}

@Composable
fun LabelBox(hazeState: HazeState, backgroundColor: Color, contentColor: Color, label: String) {
    LabelBox(hazeState, backgroundColor = backgroundColor, contentColor = contentColor) { append(label) }
}

@Composable
fun LabelBox(hazeState: HazeState, shape: Shape = MaterialTheme.shapes.small, backgroundColor: Color, contentColor: Color, builder: (Builder).() -> Unit) {
    Surface(
        color = Color.Transparent,
        contentColor = contentColor,
        shape = shape,
        modifier = Modifier
            .clip(shape)
            .hazeEffect(state = hazeState, style = HazeMaterials.regular(backgroundColor).copy(blurRadius = 4.dp)),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Text(
                modifier = Modifier
                    .defaultMinSize(minWidth = 28.dp)
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                text = buildAnnotatedString { builder() },
                style = MaterialTheme.typography.labelSmall,
                inlineContent = mapOf(
                    "icon_seeder" to InlineTextContent(
                        placeholder = androidx.compose.ui.text.Placeholder(
                            width = 1.em,
                            height = 1.em,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.North,
                            contentDescription = "seeders"
                        )
                    },
                    "icon_leecher" to InlineTextContent(
                        placeholder = androidx.compose.ui.text.Placeholder(
                            width = 1.em,
                            height = 1.em,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.South,
                            contentDescription = "leecher"
                        )
                    }
                )

            )
        }
    }
}