@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.search

//import io.github.bkmioa.nexusrss.LocalNavController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.widget.SearchBar

@Destination<RootGraph>
@Composable
fun SearchScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val viewModel: SearchViewModel = mavericksViewModel()

    val uiState by viewModel.collectAsState()

    var showFilter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                actions = {
                    Box(modifier = Modifier.weight(1.0f)) {
                        SearchBar(
                            onNavigateBack = {
                                if (showFilter) {
                                    showFilter = false
                                } else if (uiState.requestData != null && uiState.active) {
                                    viewModel.setActive(false)
                                } else {
                                    navigator.popBackStack()
                                }
                            },
                            placeholderText = stringResource(id = R.string.action_search),
                            searchText = uiState.keyword,
                            active = uiState.active,
                            onActiveChange = { viewModel.setActive(it) },
                            onQueryChange = { viewModel.setKeywords(it) },
                            onSearch = {
                                viewModel.setKeywords(it)
                                viewModel.submit()
                                showFilter = false
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.submit()
                            showFilter = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "search"
                        )
                    }
                }
            )
        },
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FilterStatus(uiState, viewModel)
                    }
                    IconButton(
                        onClick = {
                            showFilter = !showFilter
                            if (!showFilter) {
                                viewModel.submit()
                            }
                            viewModel.setActive(false)
                        }
                    ) {
                        if (showFilter) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = "filter done")
                        } else {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "filter")
                        }

                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp), thickness = 1.dp)
                Box {
                    val requestData = uiState.requestData
                    if (requestData != null) {
                        Box(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
                            ThreadList(requestData = requestData)
                        }
                    }
                    this@Column.AnimatedVisibility(
                        visible = uiState.active,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        HistoryList(uiState, viewModel)
                    }
                    this@Column.AnimatedVisibility(
                        visible = showFilter,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        FilterPanel(uiState, viewModel)
                    }

                }
            }
        }
    }
}

@Composable
private fun FilterStatus(uiState: UiState, viewModel: SearchViewModel) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "category") {
            var showCategoryList by remember { mutableStateOf(false) }
            Box(modifier = Modifier.animateItem()) {
                InputChip(
                    onClick = {
                        showCategoryList = true
                    },
                    label = {
                        Text(
                            text = uiState.category.des,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    },
                    selected = true,
                    shape = CircleShape,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "select category",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .width(96.dp)
                )
                DropdownMenu(
                    expanded = showCategoryList,
                    onDismissRequest = { showCategoryList = false }
                ) {
                    Category.ALL_CATEGORY.forEach { cat ->
                        DropdownMenuItem(text = { Text(text = cat.des) }, onClick = {
                            showCategoryList = false
                            viewModel.setCategory(cat)
                            viewModel.submit()
                        })
                    }
                }
            }
        }

        fun repeatItems(items: Set<Option>, onClick: (Option) -> Unit) {
            items(items.toList(), key = { it.id }) { item ->
                InputChip(
                    onClick = { onClick(item) },
                    label = { Text(text = item.des) },
                    selected = true,
                    shape = CircleShape,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "remove ${item.des}",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
        repeatItems(uiState.categories) { viewModel.selectCategory(it, false) }
        repeatItems(uiState.standards) { viewModel.selectStandard(it, false) }
        repeatItems(uiState.videoCodecs) { viewModel.selectVideoCodec(it, false) }
        repeatItems(uiState.audioCodecs) { viewModel.selectAudioCodec(it, false) }
        repeatItems(uiState.processings) { viewModel.selectProcessing(it, false) }
        repeatItems(uiState.teams) { viewModel.selectTeam(it, false) }
        repeatItems(uiState.labels) { viewModel.selectLabel(it, false) }
        repeatItems(setOfNotNull(uiState.discount)) { viewModel.setDiscount(null) }

    }
}

@Composable
private fun FilterPanel(uiState: UiState, viewModel: SearchViewModel) {
    Card(
        modifier = Modifier
            .fillMaxSize()
        //.padding(8.dp)
        //.requiredHeight(100.dp)
        ,
        shape = MaterialTheme.shapes.large.copy(topStart = CornerSize(0), topEnd = CornerSize(0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        OptionsUI(
            category = uiState.category,
            onMainCategoryChange = viewModel::setCategory,
            selectedCategories = uiState.categories,
            onSelectCategory = { option, selected ->
                viewModel.selectCategory(option, selected)
            },
            selectedStandards = uiState.standards,
            onSelectStandard = { option, selected ->
                viewModel.selectStandard(option, selected)
            },
            selectedVideoCodecs = uiState.videoCodecs,
            onSelectVideoCodec = { option, selected ->
                viewModel.selectVideoCodec(option, selected)
            },
            selectedAudioCodecs = uiState.audioCodecs,
            onSelectAudioCodec = { option, selected ->
                viewModel.selectAudioCodec(option, selected)
            },
            selectedProcessings = uiState.processings,
            onSelectProcessing = { option, selected ->
                viewModel.selectProcessing(option, selected)
            },
            selectedTeams = uiState.teams,
            onSelectTeam = { option, selected ->
                viewModel.selectTeam(option, selected)
            },
            selectedLabels = uiState.labels,
            onSelectLabel = { option, selected ->
                viewModel.selectLabel(option, selected)
            },
            selectedDiscount = uiState.discount,
            onSelectDiscount = { option, selected ->
                viewModel.setDiscount(option)
            },
        )
    }
}

@Composable
private fun HistoryList(uiState: UiState, viewModel: SearchViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(uiState.filteredList, key = { it }) { item ->
            val dismissBoxState = rememberSwipeToDismissBoxState(confirmValueChange = {
                if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                    viewModel.removeHistory(item)
                    true
                } else {
                    false
                }
            })
            SwipeToDismissBox(
                state = dismissBoxState,
                backgroundContent = {},
                modifier = Modifier.animateItem()
            ) {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            viewModel.setKeywords(item)
                            viewModel.submit()
                        },
                    headlineContent = { Text(text = item) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "",
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { viewModel.setKeywords(item) }) {
                            Icon(
                                imageVector = Icons.Default.NorthWest,
                                contentDescription = "",
                            )
                        }
                    }
                )
            }
        }
    }
}
