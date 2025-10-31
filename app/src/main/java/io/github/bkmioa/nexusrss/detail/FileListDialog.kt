@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.widget.Empty

val iconInlineContent = mapOf(
    "folder" to InlineTextContent(
        placeholder = androidx.compose.ui.text.Placeholder(
            width = 1.em,
            height = 1.em,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = "folder",
        )
    },
    "folder-open" to InlineTextContent(
        placeholder = androidx.compose.ui.text.Placeholder(
            width = 1.em,
            height = 1.em,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.FolderOpen,
            contentDescription = "folder open",
        )
    }
)

@Composable
fun FileListDialog(fileList: Async<List<FileNode>>, onDismissRequest: () -> Unit, onRetry: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(R.string.file_list),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            val collapsedState = remember { mutableStateMapOf<FileNode, Boolean>() }

            when (fileList) {
                is Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        fileItems(fileList(), collapsedState)
                    }
                }

                is Loading, Uninitialized -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Fail -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Empty(
                            message = fileList.error.message ?: "Unknown error",
                            actionText = stringResource(R.string.retry)
                        ) {
                            onRetry()
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.fileItems(items: List<FileNode>, collapsedState: MutableMap<FileNode, Boolean>) {
    for (node in items) {
        fileItem(node, collapsedState)
    }
}

private fun LazyListScope.fileItem(node: FileNode, collapsedState: MutableMap<FileNode, Boolean>) {
    val indent = "  ".repeat(node.depth.coerceAtLeast(0))
    if (node.isDirectory) {
        val isCollapsed = collapsedState.getOrDefault(node, false)
        item(key = System.identityHashCode(node)) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .clickable(onClick = { collapsedState.put(node, !isCollapsed) }),
                text = buildAnnotatedString {
                    if (node.depth != 0) {
                        append(indent)
                        append("├")
                    }
                    appendInlineContent(if (isCollapsed) "folder" else "folder-open")
                    append(node.name)
                },
                inlineContent = iconInlineContent
            )
        }
        if (!isCollapsed && !node.children.isEmpty()) {
            fileItems(node.children, collapsedState)
        }
    } else {
        item(key = System.identityHashCode(node)) {
            Text(
                modifier = Modifier.animateItem(),
                text = buildAnnotatedString {
                    if (node.depth != 0) {
                        append(indent)
                        append("├")
                    }
                    append(node.name)
                    if (!node.sizeText.isNullOrBlank()) {
                        append(" ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("(${node.sizeText})")
                        }
                    }
                },
                inlineContent = iconInlineContent
            )
        }
    }
}
