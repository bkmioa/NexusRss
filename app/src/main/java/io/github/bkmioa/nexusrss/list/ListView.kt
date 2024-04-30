package io.github.bkmioa.nexusrss.list

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.ui.DetailActivity
import io.github.bkmioa.nexusrss.widget.pullrefresh.PullRefreshIndicator
import io.github.bkmioa.nexusrss.widget.pullrefresh.pullRefresh
import io.github.bkmioa.nexusrss.widget.pullrefresh.rememberPullRefreshState

@Composable
fun ThreadList(
    requestData: RequestData,
    columns: Int = 0,
    visible: Boolean = true,
    keyFactory: () -> String = { "default" },
    requestScrollToTop: Boolean = false
) {
    if (!visible) {
        return
    }

    val viewModel: ListViewModel = mavericksViewModel(argsFactory = { requestData }, keyFactory = keyFactory)
    val state by viewModel.collectAsState()
    val lazyPagingItems = viewModel.pagerFlow.collectAsLazyPagingItems()
    val refreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    val refreshState = rememberPullRefreshState(refreshing, {
        viewModel.refresh()
    })
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    Log.d("ListViewModel", "CardList() called with: requestData = ${requestData.mode}, ${lifecycle.currentState}")
    LaunchedEffect(key1 = requestData) {
        Log.d("ListViewModel", "CardList() called with:  keyFactory = ${keyFactory()}")
        viewModel.request(requestData)
    }
    Box(
        Modifier
            .pullRefresh(refreshState)
            .fillMaxSize()
    ) {
        List(lazyPagingItems, requestScrollToTop, columns)

        val refresh = lazyPagingItems.loadState.refresh
        if (refresh is LoadState.Error) {
            val context = LocalContext.current
            val message = refresh.error.message ?: "Unknown error"
            if (lazyPagingItems.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorLayout(message = message) {
                        lazyPagingItems.retry()
                    }
                }
            } else {
                LaunchedEffect(refresh.error) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        PullRefreshIndicator(
            refreshing, refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun List(lazyPagingItems: LazyPagingItems<Item>, requestScrollToTop: Boolean, columns: Int = 0) {
    val gridState = rememberLazyGridState()
    LaunchedEffect(requestScrollToTop) {
        if (requestScrollToTop) {
            if (gridState.firstVisibleItemIndex == 0) {
                lazyPagingItems.refresh()
            } else {
                gridState.animateScrollToItem(0)
            }

        }
    }
    if (columns == 0) {
        GridCells.Adaptive(minSize = 160.dp)
    } else {
        GridCells.Fixed(columns)
    }
    val aspectRatio = if (columns == 1) 16 / 9f else 3 / 4f
    LazyVerticalGrid(
        columns = if (columns == 0) GridCells.Adaptive(minSize = 160.dp) else GridCells.Fixed(columns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            span = { index ->
                val item = lazyPagingItems.peek(index)
                if (item?.status?.toppingLevel != 0) {
                    GridItemSpan((maxLineSpan / 2).coerceAtLeast(2))
                } else {
                    GridItemSpan(1)
                }
            }
        ) { index ->
            val item = lazyPagingItems[index]
            if (item?.status?.toppingLevel != 0) {
                TopItemCard(item)
            } else {
                ItemCard(item, aspectRatio)
            }
        }

        val append = lazyPagingItems.loadState.append
        if (append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
        if (append is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorLayout(append.error.message ?: "Unknown error") {
                    lazyPagingItems.retry()
                }
            }
        }
    }
}

@Composable
fun TopItemCard(item: Item?) {
    item ?: return

    val containerColor = when (item.status.toppingLevel) {
        1 -> MaterialTheme.colorScheme.secondaryContainer
        2 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small,
        onClick = {
            context.startActivity(DetailActivity.createIntent(context, item))
        }
    ) {
        Row {
            AsyncImage(
                model = item.imageUrl,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(3 / 4f),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "[${item.sizeText}] ${item.subTitle ?: ""}",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }


    }
}

@Composable
fun ErrorLayout(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(text = "重试")
        }
    }
}

@Composable
fun ItemCard(item: Item?, aspectRatio: Float = 3 / 4f) {
    item ?: return
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        onClick = {
            context.startActivity(DetailActivity.createIntent(context, item))
        }
    ) {
        Box(Modifier.fillMaxWidth()) {
            AsyncImage(
                model = item.imageUrl,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(MaterialTheme.shapes.medium.copy(topStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)))
                    .height(24.dp),
            ) {
                Box(
                    modifier = Modifier
                        //.blur(4.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                        .shadow(8.dp)
                        .matchParentSize()
                )
                Row(
                    modifier = Modifier
                        .defaultMinSize(32.dp)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = Icons.Filled.North,
                        contentDescription = "seeders"
                    )
                    Text(text = item.status.seeders, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = Icons.Filled.South,
                        contentDescription = "leechers"
                    )
                    Text(text = item.status.leechers, style = MaterialTheme.typography.bodySmall)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text(
                        text = item.title,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "[${item.sizeText}] ${item.subTitle ?: ""}",
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        }
    }
}