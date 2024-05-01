@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.bkmioa.nexusrss.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.download.RemoteDownloader
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.Item
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(id: String, initialItem: Item? = null) {
    val viewModel: DetailViewModel = mavericksViewModel(argsFactory = { Pair(id, initialItem) })
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navController = LocalNavController.current

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
                            text = text,
                            modifier = Modifier
                                .padding(8.dp),
                        )
                    }
                }
            }
        }
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
