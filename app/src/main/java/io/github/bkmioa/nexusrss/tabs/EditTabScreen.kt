@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.model.Mode
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.option.OptionInitArgs
import io.github.bkmioa.nexusrss.option.OptionViewModel
import io.github.bkmioa.nexusrss.option.OptionsUI

@Destination<RootGraph>
@Composable
fun EditTabScreen(navigator: DestinationsNavigator, tab: Tab = Tab.EMPTY) {
    val editTabViewModel: EditTabViewModel = mavericksViewModel(argsFactory = { tab })
    val editTabUiState by editTabViewModel.collectAsState()

    val optionViewModel: OptionViewModel = mavericksViewModel(argsFactory = {
        OptionInitArgs(
            mode = Mode.fromMode(tab.mode),
            categories = Option.ALL_CATEGORY.filter { tab.categories.contains(it.value) }.toSet(),
            standards = Option.STANDARDS.filter { tab.standards?.contains(it.value) == true }.toSet(),
            videoCodecs = Option.VIDEOCODECS.filter { tab.videoCodecs?.contains(it.value) == true }.toSet(),
            audioCodecs = Option.AUDIOCODECS.filter { tab.audioCodecs?.contains(it.value) == true }.toSet(),
            processings = Option.PROCESSINGS.filter { tab.processings?.contains(it.value) == true }.toSet(),
            teams = Option.TEAMS.filter { tab.teams?.contains(it.value) == true }.toSet(),
            labels = Option.LABELS.filter { tab.labels?.contains(it.value) == true }.toSet(),
            discount = Option.DISCOUNTS.firstOrNull { tab.discount == it.value }
        )
    })

    val optionUiState by optionViewModel.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(stringResource(R.string.tab_edit))
                },
                actions = {
                    val scope = rememberCoroutineScope()
                    IconButton(
                        onClick = {
                            editTabViewModel.save(
                                mode = optionUiState.mode,
                                categories = optionUiState.categories,
                                standards = optionUiState.standards,
                                videoCodecs = optionUiState.videoCodecs,
                                audioCodecs = optionUiState.audioCodecs,
                                processings = optionUiState.processings,
                                teams = optionUiState.teams,
                                labels = optionUiState.labels,
                                discount = optionUiState.discount
                            )
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OptionsUI(viewModel = optionViewModel) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = editTabUiState.tab.title,
                    onValueChange = { editTabViewModel.updateTitle(it) },
                    label = { Text(stringResource(R.string.tab_title)) },
                    singleLine = true,
                )
            }
        }
    }
}