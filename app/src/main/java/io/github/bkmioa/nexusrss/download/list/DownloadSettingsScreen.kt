@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.download.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditDownloadNodeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R

@Destination<RootGraph>
@Composable
fun DownloadSettingsScreen(navigator: DestinationsNavigator) {
    val viewModel: DownloadSettingsViewModel = mavericksViewModel()
    val uiState by viewModel.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(uiState.undoDeleteNode) {
        if (uiState.undoDeleteNode != null) {
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
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                title = { Text(stringResource(R.string.download_settings)) },
                actions = {
                    IconButton(onClick = {
                        navigator.navigate(EditDownloadNodeScreenDestination())
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NodeList(viewModel, uiState)
        }
    }
}

@Composable
private fun NodeList(viewModel: DownloadSettingsViewModel, uiState: UiState) {
    val navigator = LocalNavController.current.rememberDestinationsNavigator()
    LazyColumn() {
        items(uiState.downloadNodes) {
            ListItem(
                modifier = Modifier.clickable(
                    enabled = true,
                    onClick = {
                        navigator.navigate(EditDownloadNodeScreenDestination(it.id?.toString()))
                    }
                ),
                headlineContent = { Text(it.name) },
                supportingContent = { Text(text = it.host) },
                trailingContent = {
                    var showMoreMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "actions")
                        DropdownMenu(showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_duplicate)) },
                                onClick = {
                                    viewModel.duplicateNode(it)
                                    showMoreMenu = false
                                })
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_delete)) },
                                onClick = {
                                    viewModel.deleteNode(it)
                                    showMoreMenu = false
                                })
                        }
                    }
                }
            )
        }
    }
}