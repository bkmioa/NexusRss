@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.tabs

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditTabScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.model.Tab
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Destination<RootGraph>
@Composable
fun TabsScreen(navigator: DestinationsNavigator) {
    val viewModel: TabsViewModel = mavericksViewModel()
    val uiState by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    LaunchedEffect(uiState.undoDelete) {
        if (uiState.undoDelete != null) {
            val result = snackbarHostState
                .showSnackbar(
                    message = context.getString(R.string.deleted),
                    actionLabel = context.getString(R.string.undo_action),
                    duration = SnackbarDuration.Long
                )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.performUndoDelete()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.action_tabs))
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val lastOrder = uiState.tabs.lastOrNull()?.order ?: 0
                        navigator.navigate(EditTabScreenDestination(Tab.EMPTY.copy(order = lastOrder + 1)))
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(modifier = Modifier.padding(it)) {
            val hapticFeedback = LocalHapticFeedback.current

            val lazyListState = rememberLazyListState()
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                viewModel.reorderTabs(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            LazyColumn(state = lazyListState) {
                items(uiState.tabs, key = { it.id ?: 0L }) { tab ->
                    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it != SwipeToDismissBoxValue.Settled) {
                                viewModel.removeTab(tab)
                                true
                            } else {
                                false
                            }
                        }
                    )
                    SwipeToDismissBox(state = swipeToDismissBoxState, backgroundContent = {}) {
                        ReorderableItem(reorderableLazyListState, key = tab.id ?: 0L) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                            Surface(shadowElevation = elevation) {
                                ListItem(
                                    modifier = Modifier
                                        .clickable(
                                            true,
                                            onClick = { navigator.navigate(EditTabScreenDestination(tab)) }
                                        )
                                        .longPressDraggableHandle(
                                            onDragStarted = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                            },
                                            onDragStopped = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                            },
                                        ),
                                    headlineContent = {
                                        Text(text = tab.title)
                                    },
                                    trailingContent = {
                                        Switch(checked = tab.isShow, onCheckedChange = {
                                            viewModel.update(tab.copy(isShow = it));
                                        })
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}