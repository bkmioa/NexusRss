@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    onNavigateBack: () -> Unit = {},
    placeholderText: String = "",
    searchText: String = "",
    active: Boolean = true,
    onActiveChange: (Boolean) -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = searchText, TextRange(searchText.length))) }

    LaunchedEffect(searchText) {
        if (searchText != textFieldValueState.text) {
            textFieldValueState = TextFieldValue(text = searchText, TextRange(searchText.length))
        }
    }

    fun clear() {
        textFieldValueState = TextFieldValue()
        onQueryChange("")
    }

    fun back() {
        onNavigateBack()
    }

    BackHandler() {
        back()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = ::back, modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        OutlinedTextField(
            value = textFieldValueState,
            onValueChange = {
                textFieldValueState = it
                onQueryChange(it.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .onFocusChanged { focusState ->
                    onActiveChange(focusState.isFocused)
                }
                .focusRequester(focusRequester),
            placeholder = { Text(text = placeholderText) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent,
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (textFieldValueState.text.isNotBlank()) {
                            clear()
                        } else {
                            back()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "clear"
                    )
                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                onSearch(textFieldValueState.text)
            }),
        )
    }
    LaunchedEffect(active) {
        if (active) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus(true)
        }
    }
}