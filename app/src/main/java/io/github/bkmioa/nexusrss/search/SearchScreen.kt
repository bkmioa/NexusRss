@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)

package io.github.bkmioa.nexusrss.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.widget.SearchBar

@Composable
fun SearchScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val viewModel: SearchViewModel = mavericksViewModel()

    val uiState by viewModel.collectAsState()

    var showFilter by remember { mutableStateOf(false) }

    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                actions = {
                    Box(modifier = Modifier.weight(1.0f)) {
                        SearchBar(
                            onNavigateBack = {
                                if (uiState.searchText.isNotBlank() && uiState.active) {
                                    viewModel.setActive(false)
                                } else {
                                    navController.popBackStack()
                                }
                            },
                            placeholderText = stringResource(id = R.string.action_search),
                            searchText = uiState.text,
                            active = uiState.active,
                            onActiveChange = { viewModel.setActive(it) },
                            onQueryChange = { viewModel.onQueryChange(it) },
                            onSearch = {
                                viewModel.onSearch(it)
                            }
                        )
                    }
                    IconButton(onClick = { showFilter = !showFilter }) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "filter")
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            var mode by remember { mutableStateOf(Category.NORMAL.path) }
            val requestData by remember {
                derivedStateOf {
                    RequestData(keyword = uiState.searchText, mode = mode)
                }
            }
            if (uiState.searchText.isNotBlank()) {
                ThreadList(requestData)
            }

            if (uiState.active) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LazyColumn() {
                        items(uiState.filteredList) { item ->
                            ListItem(
                                modifier = Modifier.clickable { viewModel.onSearch(item) },
                                headlineContent = { Text(text = item) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "",
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { viewModel.onQueryChange(item) }) {
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

            AnimatedVisibility(
                visible = showFilter,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Filter(mode = mode) { mode = it }
            }
        }
    }
}

@Composable
fun Filter(mode: String, onModeChanged: (String) -> Unit) {
    var selected by remember { mutableStateOf(mode) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .requiredHeight(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            FlowRow {
                listOf(Category.NORMAL, Category.MOVIE, Category.TV, Category.MUSIC, Category.ADULT).forEach {
                    FilterChip(
                        selected = selected == it.path,
                        onClick = {
                            selected = it.path
                            onModeChanged(selected)
                        },
                        label = { Text(text = it.des) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}
