@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.tabs

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
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
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.performUndoDelete()
                SnackbarResult.Dismissed -> viewModel.resetUndoDelete()
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
            val scope = rememberCoroutineScope()
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.tabs, key = { it.id ?: 0L }) { tab ->
                    val dismissBoxState = rememberSwipeToDismissBoxState()
                    if (dismissBoxState.currentValue == SwipeToDismissBoxValue.Settled) {
                        LaunchedEffect(Unit) {
                            dismissBoxState.reset()
                        }
                    }
                    ReorderableItem(reorderableLazyListState, key = tab.id ?: 0L) {
                        SwipeToDismissBox(
                            state = dismissBoxState,
                            backgroundContent = {
                                ListItem(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    colors = ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        leadingIconColor = MaterialTheme.colorScheme.onError,
                                        trailingIconColor = MaterialTheme.colorScheme.onError
                                    ),
                                    leadingContent = { Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "") },
                                    trailingContent = { Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "") },
                                    headlineContent = {}
                                )
                            },
                            onDismiss = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.removeTab(tab)
                                } else {
                                    scope.launch { dismissBoxState.reset() }
                                }
                            }
                        ) {
                            val interactionSource = remember { MutableInteractionSource() }
                            val startDrag = dismissBoxState.dismissDirection != SwipeToDismissBoxValue.Settled
                            val interaction = remember { DragInteraction.Start() }
                            LaunchedEffect(startDrag) {
                                if (startDrag) {
                                    interactionSource.emit(interaction)
                                } else {
                                    interactionSource.emit(DragInteraction.Cancel(interaction))

                                }
                            }
                            Card(
                                interactionSource = interactionSource,
                                shape = RoundedCornerShape(0.dp),
                                onClick = {
                                    navigator.navigate(EditTabScreenDestination(tab))
                                }
                            ) {
                                ListItem(
                                    modifier = Modifier
                                        .longPressDraggableHandle(
                                            onDragStarted = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                            },
                                            onDragStopped = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                            },
                                            interactionSource = interactionSource,
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