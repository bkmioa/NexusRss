package io.github.bkmioa.nexusrss.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
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
        if (!alignEndSeederAndLeecher) {
            SeederAndLeecher(hazeState, item)
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
            LabelBox(hazeState = hazeState, backgroundColor = color.first, contentColor = color.second, label = label.uppercase())
        }
        if (alignEndSeederAndLeecher) {
            SeederAndLeecher(hazeState, item)
        }
    }
}

@Composable
fun SeederAndLeecher(hazeState: HazeState, item: Item) {
    ConstraintLayout {
        val (seeder, leecher) = createRefs()
        LabelBox(
            modifier = Modifier
                .padding(end = 4.dp)
                .constrainAs(seeder) {

                },
            hazeState = hazeState,
            shape = MaterialTheme.shapes.small.copy(topEnd = ZeroCornerSize, bottomEnd = ZeroCornerSize),
            backgroundColor = colorResource(R.color.label_bg_up),
            contentColor = colorResource(R.color.label_fg_up)
        ) {
            appendInlineContent("icon_seeder")
            append(item.status.seeders)
        }
        LabelBox(
            modifier = Modifier
                .padding(start = 4.dp)
                .constrainAs(leecher) {
                    start.linkTo(seeder.end, margin = (-8).dp)
                },
            hazeState = hazeState,
            shape = SlantedRightPartShape(slant = 8.dp, 8.dp),
            backgroundColor = colorResource(R.color.label_bg_down),
            contentColor = colorResource(R.color.label_fg_down)
        ) {
            appendInlineContent("icon_leecher")
            append(item.status.leechers)
        }
    }
}

@Composable
fun LabelBox(modifier: Modifier = Modifier, hazeState: HazeState, shape: Shape = MaterialTheme.shapes.small, backgroundColor: Color, contentColor: Color, label: String) {
    LabelBox(modifier = modifier, hazeState = hazeState, shape = shape, backgroundColor = backgroundColor, contentColor = contentColor) { append(label) }
}

@Composable
fun LabelBox(modifier: Modifier = Modifier, hazeState: HazeState, shape: Shape = MaterialTheme.shapes.small, backgroundColor: Color, contentColor: Color, builder: (Builder).() -> Unit) {
    Surface(
        color = Color.Transparent,
        contentColor = contentColor,
        shape = shape,
        modifier = Modifier
            .clip(shape)
            .hazeEffect(state = hazeState, style = HazeMaterials.regular(backgroundColor).copy(blurRadius = 4.dp))
            .then(modifier),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Text(
                modifier = Modifier
                    .defaultMinSize(minWidth = 24.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                textAlign = TextAlign.Center,
                text = buildAnnotatedString { builder() },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
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

@Composable
fun RatingLabels(modifier: Modifier, hazeState: HazeState, item: Item) {
    Row(
        modifier = modifier
            .padding(all = 4.dp)
    ) {
        if (!item.doubanRating.isNullOrBlank() && item.doubanRating != "0") {
            LabelBox(hazeState = hazeState, backgroundColor = colorResource(R.color.label_bg_douban), contentColor = colorResource(R.color.label_fg_douban), label = "豆 " + item.doubanRating!!)
        }
        Spacer(modifier = Modifier.width(4.dp))
        if (!item.imdbRating.isNullOrBlank() && item.imdbRating != "0") {
            LabelBox(hazeState = hazeState, backgroundColor = colorResource(R.color.label_bg_imdb), contentColor = colorResource(R.color.label_fg_imdb), label = "IMDB " + item.imdbRating!!)
        }
    }
}