@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R

@Composable
fun TabsScreen() {
    val viewModel: TabsViewModel = mavericksViewModel()
    val uiState by viewModel.collectAsState()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.action_tabs))
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            //Log.d(TabsViewModel.TAG, "getAllTabFlow() called, ${uiState.tabs.last()}")
            LazyColumn {
                items(uiState.tabs, key = { it }) { tab ->
                    ListItem(
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