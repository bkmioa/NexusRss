@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package io.github.bkmioa.nexusrss.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.comment.CommentList
import io.github.bkmioa.nexusrss.download.RemoteDownloader
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.FileItem
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.widget.Empty
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
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                var showDownload by remember { mutableStateOf(false) }
                FloatingActionButton(onClick = { showDownload = true }) {
                    Icon(imageVector = Icons.Outlined.CloudDownload, contentDescription = "comment")
                    DownLoadList(expanded = showDownload, downloadNodes = uiState.downloadNodes, getTorrentUrl = { viewModel.getDownloadLink() }) {
                        showDownload = false
                    }
                }
                FloatingActionButton(onClick = { viewModel.showFileList() }) {
                    Icon(imageVector = Icons.Outlined.Description, contentDescription = "files")
                }
                FloatingActionButton(
                    onClick = { viewModel.showCommentList() },
                    modifier = Modifier.width(80.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = item?.status?.comments ?: "0",
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(20.dp),
                        )
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = "comment"
                        )
                    }
                }
            }
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
                TitleInfo(item?.title, item?.subTitle)

                BasicInfo(item)

                //CommentInfo(item) { viewModel.showCommentList() }

                //TorrentFileList { viewModel.showFileList() }

                DetailInfo(item?.descr)

                MediaInfo(item?.mediainfo)
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

    if (uiState.showCommentList) {
        CommentListDialog(relationId = item?.id ?: "") {
            viewModel.hideCommentList()
        }
    }
}

@Composable
fun CommentInfo(item: Item?, onClick: () -> Unit) {
    item ?: return

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "评论列表 (${item.status.comments})",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "open file list",
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
private fun TitleInfo(title: String?, subTitle: String?) {
    if (title.isNullOrBlank()) return

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            title,
            modifier = Modifier
                .padding(8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        if (!subTitle.isNullOrBlank()) {
            Text(
                subTitle,
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun BasicInfo(item: Item?) {
    if (item == null) return
    val context = LocalContext.current
    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column() {
            Text(
                modifier = Modifier.padding(8.dp),
                text = buildAnnotatedString {
                    append("由 ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(item.getAuthorText(context))
                    }
                    append(" 发布于 ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(item.formatRelativeDateText())
                    }
                },
            )
            FlowRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoLabel("大小", item.sizeText)
                InfoLabel("類別", Option.ALL.find { it.value == item.category }?.des)
                InfoLabel("視頻編碼", Option.VIDEOCODECS.find { it.value == item.videoCodec }?.des)
                InfoLabel("音頻編碼", Option.AUDIOCODECS.find { it.value == item.audioCodec }?.des)
                InfoLabel("解析度", Option.STANDARDS.find { it.value == item.standard }?.des)
                InfoLabel("地區", Option.PROCESSINGS.find { it.value == item.processing }?.des)
                InfoLabel("製作組:", Option.TEAMS.find { it.value == item.team }?.des)
            }
        }
    }
}

@Composable
fun InfoLabel(title: String, text: String?) {
    if (text.isNullOrBlank()) return

    Text(text = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("$title: ")
        }
        append(text)
    })
}

@Composable
private fun TorrentFileList(onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "種子檔案",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "open file list",
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
private fun DetailInfo(detailInfo: String?) {
    if (detailInfo.isNullOrBlank()) return

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        DetailWebView(data = detailInfo)
    }
}

@Composable
private fun MediaInfo(mediainfo: String?) {
    if (mediainfo.isNullOrBlank()) return

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = mediainfo.replace("  +".toRegex(), " "),
            modifier = Modifier
                .padding(8.dp),
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
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxHeight()
    ) {
        Text(
            text = "種子檔案",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (fileList) {
                is Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
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
                        Empty(message = fileList.error.message ?: "Unknown error") {
                            onRetry()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentListDialog(relationId: String, onDismissRequest: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .fillMaxHeight()
    ) {
        Text(
            text = "评论列表",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        CommentList(relationId, lifecycleOwner)
    }
}
