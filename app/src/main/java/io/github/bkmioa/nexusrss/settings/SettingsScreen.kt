@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings

@Composable
fun SettingsScreen() {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.action_settings))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var baseUrlTextFieldValue by remember {
                    mutableStateOf(TextFieldValue(Settings.BASE_URL, TextRange(Settings.BASE_URL.length)))
                }

                OutlinedTextField(
                    value = baseUrlTextFieldValue,
                    onValueChange = { value ->
                        baseUrlTextFieldValue = value
                        Settings.BASE_URL = value.text.trim().removeSuffix("/")
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = R.string.base_url))
                    }
                )

                var apiKeyTextFieldValue by remember {
                    mutableStateOf(TextFieldValue(Settings.API_KEY, TextRange(Settings.API_KEY.length)))
                }
                OutlinedTextField(
                    value = apiKeyTextFieldValue,
                    onValueChange = { value ->
                        apiKeyTextFieldValue = value
                        Settings.API_KEY = apiKeyTextFieldValue.text.trim()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(text = "x-api-key")
                    }
                )
            }
        }
    }
}