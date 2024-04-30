@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package io.github.bkmioa.nexusrss.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.model.RequestData

@Composable
fun SearchScreen(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var text by rememberSaveable { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {},
                )
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = SearchBarDefaults.colors(containerColor = Color.Transparent),
                    query = text,
                    placeholder = { Text(text = stringResource(id = R.string.action_search)) },
                    onQueryChange = {
                        text = it
                    },
                    onSearch = {
                        searchText = text
                    },
                    active = false,
                    onActiveChange = {},
                    leadingIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = {
                                if (text.isNotEmpty()) {
                                    text = ""
                                } else {
                                    navController.popBackStack()
                                }

                            }) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "clear")
                            }
                            IconButton(onClick = { showFilter = !showFilter }) {
                                Icon(imageVector = Icons.Default.FilterList, contentDescription = "filter")
                            }
                        }
                    }
                ) {
                    ListItem(
                        headlineContent = { Text(text = "xxx") },
                        leadingContent = { Icon(imageVector = Icons.Default.History, contentDescription = "") }
                    )
                }
            }
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
                    RequestData(keyword = searchText, mode = mode)
                }
            }
            if (searchText.isNotBlank()) {
                ThreadList(requestData)
            }
            val density = LocalDensity.current
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
