@file:OptIn(ExperimentalLayoutApi::class)

package io.github.bkmioa.nexusrss.option

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option


@Composable
fun OptionsUI(
    modifier: Modifier = Modifier,
    viewModel: OptionViewModel = mavericksViewModel(),
    header: (@Composable () -> Unit)? = null,
) {

    val uiState by viewModel.collectAsState()
    val context = LocalContext.current

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (header != null) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "header") {
                header()
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "category_label") {
            Text(text = stringResource(R.string.label_category), style = MaterialTheme.typography.headlineSmall)
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "category") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Mode.ALL.forEach {
                    FilterChip(
                        selected = uiState.mode == it,
                        onClick = {
                            viewModel.setMode(it)
                        },
                        label = { Text(text = it.des) },
                        shape = CircleShape
                    )
                }
            }
        }
        items(uiState.mode.options, key = { it.id }) { option ->
            val selected = uiState.categories.contains(option)
            FilterChip(
                selected = selected,
                onClick = { viewModel.selectCategory(option, !selected) },
                leadingIcon = {
                    AsyncImage(
                        model = Settings.BASE_URL + option.img,
                        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                        error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(24.dp)
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.extraSmall),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                },
                label = {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = option.des,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        }

        fun headLine(text: String) {
            item(span = { GridItemSpan(maxLineSpan) }, key = text) {
                Text(text = text, style = MaterialTheme.typography.headlineSmall)
            }
        }

        fun repeatOptions(options: List<Option>, selected: Set<Option?>, onSelect: (option: Option, selected: Boolean) -> Unit) {
            items(options, key = { it.id }) { option ->
                val selected = selected.contains(option)
                FilterChip(
                    selected = selected,
                    onClick = { onSelect(option, !selected) },
                    label = {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = option.des, textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
        }

        headLine(context.getString(R.string.label_resolution))
        repeatOptions(Option.STANDARDS, uiState.standards, viewModel::selectStandard)

        headLine(context.getString(R.string.label_video_codec))
        repeatOptions(Option.VIDEOCODECS, uiState.videoCodecs, viewModel::selectVideoCodec)

        headLine(context.getString(R.string.label_label))
        repeatOptions(Option.LABELS, uiState.labels, viewModel::selectLabel)

        headLine(context.getString(R.string.label_discount))
        repeatOptions(Option.DISCOUNTS, setOf(uiState.discount), viewModel::setDiscount)

        headLine(context.getString(R.string.label_audio_codec))
        repeatOptions(Option.AUDIOCODECS, uiState.audioCodecs, viewModel::selectAudioCodec)

        headLine(context.getString(R.string.label_region))
        repeatOptions(Option.PROCESSINGS, uiState.processings, viewModel::selectProcessing)

        headLine(context.getString(R.string.label_team))
        repeatOptions(Option.TEAMS, uiState.teams, viewModel::selectTeam)
    }
}