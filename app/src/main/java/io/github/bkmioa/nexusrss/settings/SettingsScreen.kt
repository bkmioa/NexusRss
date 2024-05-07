@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import kotlinx.coroutines.launch

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
                    placeholder = {
                        Text(text = Settings.DEFAULT_BASE_URL)
                    },
                    onValueChange = { value ->
                        baseUrlTextFieldValue = value
                        val url = value.text.trim().removeSuffix("/")
                        Settings.BASE_URL = url.takeIf { it.isNotBlank() } ?: Settings.DEFAULT_BASE_URL
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
                var showApiKey by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = apiKeyTextFieldValue,
                    onValueChange = { value ->
                        apiKeyTextFieldValue = value
                        Settings.API_KEY = apiKeyTextFieldValue.text.trim()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(text = "API Key")
                    },
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        val tooltipState = rememberTooltipState(isPersistent = true)
                        val scope = rememberCoroutineScope()
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                            tooltip = {
                                RichTooltip(title = { Text("如何获取 API Key") }) {
                                    Text("请前往 Web 站点登录后，前往 控制台 -> 實驗室 -> 存取令牌 获取 API Key")
                                }
                            },
                            state = tooltipState
                        ) {
                            IconButton(
                                onClick = { scope.launch { tooltipState.show() } }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "api key help"
                                )
                            }
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showApiKey = !showApiKey
                            }
                        ) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "visibility"
                            )
                        }

                    }
                )
            }
        }
    }
}