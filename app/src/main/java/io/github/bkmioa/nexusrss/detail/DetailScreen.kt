@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.download.RemoteDownloader
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.FileItem
import io.github.bkmioa.nexusrss.widget.ErrorLayout
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(args: Bundle) {
    val navController = LocalNavController.current
    val viewModel: DetailViewModel = mavericksViewModel(argsFactory = { args })
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val uiState by viewModel.collectAsState()

    val item = uiState.data()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Text(
                        item?.title ?: "",
                        maxLines = 1,
                        modifier = Modifier.alpha(scrollBehavior.state.overlappedFraction)
                    )
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    var showDownload by remember { mutableStateOf(false) }
                    var showMoreMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = {
                        showDownload = true
                    }) {
                        Icon(imageVector = Icons.Outlined.CloudDownload, contentDescription = "download")

                    }

                    IconButton(onClick = {
                        showMoreMenu = true
                    }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more")
                    }

                    DownLoadList(showDownload, uiState.downloadNodes,
                        getTorrentUrl = {
                            viewModel.getDownloadLink()
                        },
                        onDismissRequest = {
                            showDownload = false
                        })

                    MoreMenus(showMoreMenu,
                        link = item?.link ?: "",
                        getTorrentUrl = {
                            viewModel.getDownloadLink()
                        },
                        onDismissRequest = {
                            showMoreMenu = false
                        })
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    item?.title?.let {
                        Text(
                            it,
                            modifier = Modifier
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    item?.subTitle?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            it,
                            modifier = Modifier
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                OutlinedCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    DetailWebView(data = item?.descr)
                }
                item?.mediainfo?.let { text ->
                    OutlinedCard(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = text.replace("  +".toRegex(), " "),
                            modifier = Modifier
                                .padding(8.dp),
                        )
                    }
                }
                OutlinedCard(
                    onClick = {
                        viewModel.showFileList()
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "種子檔案",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                    )
                }
            }
        }
    }
    if (uiState.showFileList) {
        FileListDialog(
            fileList = uiState.fileList,
            onDismissRequest = {
                viewModel.hideFileList()
            },
            onRetry = {
                viewModel.fetchFileList()
            }
        )
    }
}

@Composable
fun MoreMenus(expanded: Boolean, link: String?, getTorrentUrl: suspend () -> String?, onDismissRequest: () -> Unit) {
    val composableScope = rememberCoroutineScope()

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.copy_link)) },
            onClick = {
                composableScope.launch {
                    getTorrentUrl()?.takeIf { it.isNotBlank() }?.let {
                        clipboardManager.setText(AnnotatedString(it))
                        Toast.makeText(context, R.string.copy_done, Toast.LENGTH_SHORT).show()
                    }
                }
                onDismissRequest()
            },
            modifier = Modifier.defaultMinSize(160.dp),
        )
        DropdownMenuItem(
            enabled = !link.isNullOrBlank(),
            text = { Text(text = stringResource(id = R.string.open_link)) },
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                context.startActivity(intent)
                onDismissRequest()
            },
            modifier = Modifier.defaultMinSize(160.dp),
        )
    }
}

@Composable
fun DownLoadList(
    expanded: Boolean,
    downloadNodes: List<DownloadNodeModel>,
    getTorrentUrl: suspend () -> String?,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(enabled = false,
            modifier = Modifier.defaultMinSize(160.dp),
            text = { Text(text = stringResource(id = R.string.remote_download)) },
            onClick = { }
        )

        downloadNodes.forEach { node ->
            DropdownMenuItem(
                modifier = Modifier.defaultMinSize(160.dp),
                text = { Text(text = node.name) },
                onClick = {
                    composableScope.launch {
                        getTorrentUrl()?.takeIf { it.isNotBlank() }?.let {
                            RemoteDownloader.download(context, node.toDownloadNode(), it)
                        }
                    }
                    onDismissRequest()
                })
        }
    }
}


@Composable
fun FileListDialog(fileList: Async<List<FileItem>>, onDismissRequest: () -> Unit, onRetry: () -> Unit) {
    val paddingValues = WindowInsets.systemBars.asPaddingValues()
    val topPadding = paddingValues.calculateTopPadding() / 2
    val bottomPadding = paddingValues.calculateBottomPadding()
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        windowInsets = WindowInsets(0),
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxHeight()
    ) {
        Text(
            text = "種子檔案",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .padding(bottom = bottomPadding + topPadding)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (fileList) {
                is Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(fileList()) { file ->
                            Text(
                                text = buildAnnotatedString {
                                    append(file.name)
                                    append(" ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("(${file.sizeText})")
                                    }
                                }
                            )
                        }
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
                        ErrorLayout(fileList.error.message ?: "Unknown error") {
                            onRetry()
                        }
                    }
                }
            }
        }
    }
}
