@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package io.github.bkmioa.nexusrss.home

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DownloadSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TabsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.utils.toDestinationsNavigator
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.checkversion.CheckVersionViewModel
import io.github.bkmioa.nexusrss.list.ThreadList
import io.github.bkmioa.nexusrss.model.RequestData
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val homeViewModel: HomeViewModel = mavericksViewModel()
    val uiState by homeViewModel.collectAsState()

    val checkVersionViewModel: CheckVersionViewModel = mavericksViewModel()
    val checkVersionUiState by checkVersionViewModel.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val remoteVersion = checkVersionUiState.remoteVersion
    val showSnapBar = checkVersionUiState.canUpgrade && checkVersionUiState.showSnapBar && remoteVersion != null
    LaunchedEffect(showSnapBar) {
        if (showSnapBar) {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                context.getString(R.string.new_version),
                context.getString(R.string.download),
                withDismissAction = true
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    try {
                        val htmlUrl = remoteVersion.htmlUrl!!
                        context.startActivity(Intent(Intent.ACTION_VIEW, htmlUrl.toUri()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, e.message ?: context.getString(R.string.loading_error_toast), Toast.LENGTH_SHORT).show()
                    }

                }

                SnackbarResult.Dismissed -> {
                    checkVersionViewModel.setShowSnackBar(false)
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            val defaultColors = TopAppBarDefaults.topAppBarColors()
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                scrollBehavior = scrollBehavior,
                colors = defaultColors.copy(scrolledContainerColor = defaultColors.containerColor),
                actions = {
                    var showMoreMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { navigator.navigate(SearchScreenDestination) }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "more")
                    }
                    DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.action_tabs)) },
                            onClick = {
                                navigator.navigate(TabsScreenDestination)
                                showMoreMenu = false
                            },
                            modifier = Modifier.defaultMinSize(150.dp)
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.action_settings)) },
                            onClick = {
                                navigator.navigate(SettingsScreenDestination)
                                showMoreMenu = false
                            },
                            modifier = Modifier.defaultMinSize(150.dp)
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.download_settings)) },
                            onClick = {
                                navigator.navigate(DownloadSettingsScreenDestination)
                                showMoreMenu = false
                            },
                            modifier = Modifier.defaultMinSize(150.dp)
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.action_checking_version)) },
                            onClick = {
                                checkVersionViewModel.checkVersion()
                                showMoreMenu = false
                            },
                            modifier = Modifier.defaultMinSize(150.dp)
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                val createTab: @Composable (Modifier) -> Unit = { tabModifier ->
                    Tab(
                        modifier = tabModifier,
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
                var tabContainerWidth by remember { mutableStateOf(0) }
                val tabCount = tabAndPagers.size
                val tabWidths = remember(tabCount) {
                    mutableStateListOf<Int>().apply { repeat(tabCount) { add(0) } }
                }
                val shouldUseScrollable by remember {
                    derivedStateOf {
                        tabContainerWidth == 0 ||
                                tabWidths.isEmpty() ||
                                tabWidths.any { it == 0 } ||
                                tabWidths.sum() > tabContainerWidth
                    }
                }
                val tabRowModifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onSizeChanged { size -> tabContainerWidth = size.width }

                val tabRowContent: @Composable () -> Unit = {
                    tabAndPagers.forEachIndexed { index, (tab, _) ->
                        tab(
                            Modifier.onSizeChanged { size ->
                                if (index < tabWidths.size) {
                                    tabWidths[index] = size.width
                                }
                            }
                        )
                    }
                }

                if (shouldUseScrollable) {
                    SecondaryScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage.coerceAtMost(tabs.size - 1),
                        edgePadding = 0.dp,
                        modifier = tabRowModifier,
                        containerColor = TabRowDefaults.primaryContainerColor,
                        contentColor = TabRowDefaults.primaryContentColor,
                    ) {
                        tabRowContent()
                    }
                } else {
                    SecondaryTabRow(
                        selectedTabIndex = pagerState.currentPage.coerceAtMost(tabs.size - 1),
                        modifier = tabRowModifier,
                        containerColor = TabRowDefaults.primaryContainerColor,
                        contentColor = TabRowDefaults.primaryContentColor,
                    ) {
                        tabRowContent()
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

    ApiKeyGuideIfNeeded(snackbarHostState)
}

@Composable
fun ApiKeyGuideIfNeeded(snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val navigator = LocalNavController.current.toDestinationsNavigator()
    LaunchedEffect(Settings.API_KEY) {
        if (Settings.API_KEY.isBlank()) {
            val result = snackbarHostState.showSnackbar(context.getString(R.string.api_key_guide), actionLabel = context.getString(R.string.action_settings))
            if (result == SnackbarResult.ActionPerformed) {
                navigator.navigate(SettingsScreenDestination)
            }
        }
    }
}
