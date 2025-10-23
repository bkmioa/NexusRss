@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditTabScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.model.Tab

@Destination<RootGraph>
@Composable
fun TabsScreen(navigator: DestinationsNavigator) {
    val viewModel: TabsViewModel = mavericksViewModel()
    val uiState by viewModel.collectAsState()

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
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            LazyColumn {
                items(uiState.tabs, key = { it.id ?: 0L }) { tab ->
                    ListItem(
                        modifier = Modifier.clickable(true, onClick = { navigator.navigate(EditTabScreenDestination(tab)) }),
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