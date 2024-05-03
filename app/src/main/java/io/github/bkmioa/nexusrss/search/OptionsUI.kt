@file:OptIn(ExperimentalLayoutApi::class)

package io.github.bkmioa.nexusrss.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.model.Option


@Composable
fun OptionsUI(
    modifier: Modifier = Modifier,
    category: Category,
    onMainCategoryChange: (Category) -> Unit,
    selectedCategories: Set<Option> = emptySet(),
    onSelectCategory: (Option, Boolean) -> Unit,
    selectedStandards: Set<Option> = emptySet(),
    onSelectStandard: (Option, Boolean) -> Unit,
    selectedVideoCodecs: Set<Option> = emptySet(),
    onSelectVideoCodec: (Option, Boolean) -> Unit,
    selectedAudioCodecs: Set<Option> = emptySet(),
    onSelectAudioCodec: (Option, Boolean) -> Unit,
    selectedProcessings: Set<Option> = emptySet(),
    onSelectProcessing: (Option, Boolean) -> Unit,
    selectedTeams: Set<Option> = emptySet(),
    onSelectTeam: (Option, Boolean) -> Unit,
    selectedLabels: Set<Option> = emptySet(),
    onSelectLabel: (Option, Boolean) -> Unit,
    selectedDiscount: Option? = null,
    onSelectDiscount: (Option, Boolean) -> Unit,
) {

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "category_label") {
            Text(text = "類別", style = MaterialTheme.typography.headlineSmall)
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "category") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Category.ALL_CATEGORY.forEach {
                    FilterChip(
                        selected = category == it,
                        onClick = {
                            onMainCategoryChange(it)
                        },
                        label = { Text(text = it.des) },
                        shape = CircleShape
                    )
                }
            }
        }
        items(category.options, key = { it.id }) { option ->
            val selected = selectedCategories.contains(option)
            FilterChip(
                selected = selected,
                onClick = { onSelectCategory(option, !selected) },
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
                        text = option.des,
                        minLines = 2,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelSmall,
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

        fun repeatOptions(options: List<Option>, selected: Set<Option?>, onSelect: (Option, Boolean) -> Unit) {
            items(options, key = { it.id }) { option ->
                val selected = selected.contains(option)
                FilterChip(
                    selected = selected,
                    onClick = { onSelect(option, !selected) },
                    label = { Text(text = option.des) }
                )
            }
        }

        headLine("解析度")
        repeatOptions(Option.STANDARDS, selectedStandards, onSelectStandard)

        headLine("視頻編碼")
        repeatOptions(Option.VIDEOCODECS, selectedVideoCodecs, onSelectVideoCodec)

        headLine("音頻編碼")
        repeatOptions(Option.AUDIOCODECS, selectedAudioCodecs, onSelectAudioCodec)

        headLine("地區")
        repeatOptions(Option.PROCESSINGS, selectedProcessings, onSelectProcessing)

        headLine("製作組")
        repeatOptions(Option.TEAMS, selectedTeams, onSelectTeam)

        headLine("標記")
        repeatOptions(Option.LABELS, selectedLabels, onSelectLabel)

        headLine("促銷")
        repeatOptions(Option.DISCOUNTS, setOf(selectedDiscount), onSelectDiscount)
    }
}