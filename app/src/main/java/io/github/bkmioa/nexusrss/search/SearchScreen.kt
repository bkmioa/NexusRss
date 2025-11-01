@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.search

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.option.OptionUiState
import io.github.bkmioa.nexusrss.option.OptionViewModel
import io.github.bkmioa.nexusrss.option.OptionsUI
import io.github.bkmioa.nexusrss.widget.SearchBar
import kotlinx.coroutines.launch

@Destination<RootGraph>(
    deepLinks = [
        DeepLink(uriPattern = "nexusrss://search")
    ]
)
@Composable
fun SearchScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val searchViewModel: SearchViewModel = mavericksViewModel()
    val optionViewModel: OptionViewModel = mavericksViewModel()

    val searchUiState by searchViewModel.collectAsState()
    val optionUiState by optionViewModel.collectAsState()

    var showOptionPanel by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    fun submit() = scope.launch {
        val option = optionViewModel.awaitState()
        searchViewModel.submit(
            mode = option.mode,
            categories = option.categories,
            standards = option.standards,
            videoCodecs = option.videoCodecs,
            audioCodecs = option.audioCodecs,
            processings = option.processings,
            teams = option.teams,
            labels = option.labels,
            discount = option.discount
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                actions = {
                    Box(modifier = Modifier.weight(1.0f)) {
                        SearchBar(
                            onNavigateBack = {
                                if (showOptionPanel) {
                                    showOptionPanel = false
                                } else if (searchUiState.requestData != null && searchUiState.active) {
                                    searchViewModel.setActive(false)
                                } else {
                                    navigator.popBackStack()
                                }
                            },
                            placeholderText = stringResource(id = R.string.action_search),
                            searchText = searchUiState.keyword,
                            active = searchUiState.active,
                            onActiveChange = { searchViewModel.setActive(it) },
                            onQueryChange = { searchViewModel.setKeywords(it) },
                            onSearch = {
                                searchViewModel.setKeywords(it)
                                submit()
                                showOptionPanel = false
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            submit()
                            showOptionPanel = false
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
            fun switchFilterPanel() {
                showOptionPanel = !showOptionPanel
                searchViewModel.setActive(false)
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = {
                            switchFilterPanel()
                        })
                        .padding(start = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FilterStatus(optionUiState, optionViewModel = optionViewModel) {
                            if (!showOptionPanel) {
                                submit()
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            switchFilterPanel()
                            if (!showOptionPanel) submit()
                        }
                    ) {
                        if (showOptionPanel) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = "filter done")
                        } else {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "filter")
                        }

                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp), thickness = 1.dp)
                Box {
                    val requestData = searchUiState.requestData
                    if (requestData != null) {
                        Box(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
                            ThreadList(requestData = requestData)
                        }
                    }
                    this@Column.AnimatedVisibility(
                        visible = searchUiState.active,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        HistoryList(searchUiState.filteredList, onRemove = { searchViewModel.removeHistory(it) }, onSelected = { keywords, performSearch ->
                            searchViewModel.setKeywords(keywords)
                            if (performSearch) {
                                submit()
                            }
                        })
                    }
                    this@Column.AnimatedVisibility(
                        visible = showOptionPanel,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        FilterPanel(optionViewModel)
                    }

                }
            }
        }
    }
}

@Composable
private fun FilterStatus(optionUiState: OptionUiState, optionViewModel: OptionViewModel, onOptionChanged: () -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "category") {
            var showModeList by remember { mutableStateOf(false) }
            Box(modifier = Modifier.animateItem()) {
                InputChip(
                    onClick = {
                        showModeList = true
                    },
                    label = {
                        Text(
                            text = optionUiState.mode.des,
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
                    expanded = showModeList,
                    onDismissRequest = { showModeList = false }
                ) {
                    Mode.ALL.forEach { mode ->
                        DropdownMenuItem(text = { Text(text = mode.des) }, onClick = {
                            showModeList = false
                            optionViewModel.setMode(mode)
                            onOptionChanged()
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
                            contentDescription = "remove ${item}",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
        repeatItems(optionUiState.categories) {
            optionViewModel.selectCategory(it, false)
            onOptionChanged()
        }
        repeatItems(optionUiState.standards) {
            optionViewModel.selectStandard(it, false)
            onOptionChanged()
        }
        repeatItems(optionUiState.videoCodecs) {
            optionViewModel.selectVideoCodec(it, false)
            onOptionChanged()
        }
        repeatItems(optionUiState.audioCodecs) {
            optionViewModel.selectAudioCodec(it, false)
            onOptionChanged()
        }
        repeatItems(optionUiState.processings) {
            optionViewModel.selectProcessing(it, false)
            onOptionChanged()
        }
        repeatItems(optionUiState.teams) {
            optionViewModel.selectTeam(it, false)
            onOptionChanged()
        }
        repeatItems(optionUiState.labels) {
            optionViewModel.selectLabel(it, false)
            onOptionChanged()
        }
        repeatItems(setOfNotNull(optionUiState.discount)) {
            optionViewModel.setDiscount(null, false)
            onOptionChanged()
        }

    }
}

@Composable
private fun FilterPanel(optionViewModel: OptionViewModel) {
    Card(
        modifier = Modifier
            .fillMaxSize(),
        shape = MaterialTheme.shapes.large.copy(topStart = CornerSize(0), topEnd = CornerSize(0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OptionsUI(viewModel = optionViewModel)
    }
}

@Composable
private fun HistoryList(list: List<String>, onRemove: (String) -> Unit, onSelected: (keywords: String, performSearch: Boolean) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        items(list, key = { it }) { item ->
            val dismissBoxState = rememberSwipeToDismissBoxState()
            SwipeToDismissBox(
                state = dismissBoxState,
                backgroundContent = {},
                modifier = Modifier.animateItem(),
                onDismiss = {
                    if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                        onRemove(item)
                    }
                }
            ) {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            onSelected(item, true)
                        },
                    headlineContent = { Text(text = item) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "",
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { onSelected(item, false) }) {
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
