@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.bkmioa.nexusrss.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Router
import io.github.bkmioa.nexusrss.navigate
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.RequestData
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = mavericksViewModel()
    val uiState by homeViewModel.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            val defaultColors = TopAppBarDefaults.topAppBarColors()
            TopAppBar(
                title = {
                    Text("M-Team")
                },
                scrollBehavior = scrollBehavior,
                colors = defaultColors.copy(scrolledContainerColor = defaultColors.containerColor),
                actions = {
                    var showMoreMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { navController.navigate(Router.Search) }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "more")
                    }
                    DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.action_tabs)) },
                            onClick = {
                                navController.navigate(Router.Tabs)
                                showMoreMenu = false
                            },
                            modifier = Modifier.defaultMinSize(150.dp)
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.action_settings)) },
                            onClick = {
                                navController.navigate(Router.Settings)
                                showMoreMenu = false
                            },
                            modifier = Modifier.defaultMinSize(150.dp)
                        )
                    }
                },
            )
        },
    ) {

        Box(modifier = Modifier.padding(it)) {
            val tabs = uiState.tabs
            if (tabs.isEmpty()) {
                return@Box
            }
            val pagerState = rememberPagerState(0, pageCount = { tabs.size })
            val coroutineScope = rememberCoroutineScope()

            val tabAndPagers = tabs.mapIndexed { index, tab ->
                val gridState: LazyGridState = rememberLazyGridState()
                var requestRefresh by remember { mutableStateOf(false) }
                val visibleBefore by remember { derivedStateOf { AtomicBoolean(false) } }
                val selected = index == pagerState.currentPage
                val createTab = @Composable {
                    Tab(
                        text = { Text(tab.title) },
                        selected = selected,
                        onClick = {
                            if (pagerState.currentPage == index) {
                                if (gridState.firstVisibleItemIndex != 0) {
                                    coroutineScope.launch { gridState.animateScrollToItem(0) }
                                } else {
                                    requestRefresh = true
                                }
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        }
                    )
                }
                val createPager = @Composable {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        visibleBefore.compareAndSet(false, selected)
                        ThreadList(
                            requestData = RequestData.from(tab),
                            requestRefresh = requestRefresh,
                            onRefreshed = { requestRefresh = false },
                            //tab.columnCount,
                            visible = visibleBefore.get(),
                            keyFactory = { tab.makeKey() },
                            gridState = gridState,
                        )
                    }
                }
                createTab to createPager
            }


            Column {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage.coerceAtMost(tabs.size - 1),
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                ) {
                    tabAndPagers.forEach { (tab, _) ->
                        tab()
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    key = { index -> tabs[index] },
                    pageNestedScrollConnection = scrollBehavior.nestedScrollConnection
                ) { index ->
                    tabAndPagers[index].second()
                }
            }
        }
    }
}