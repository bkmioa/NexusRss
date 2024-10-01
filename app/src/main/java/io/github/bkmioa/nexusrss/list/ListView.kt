package io.github.bkmioa.nexusrss.list

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.South
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Router
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.model.RequestData
import io.github.bkmioa.nexusrss.widget.Empty
import io.github.bkmioa.nexusrss.widget.pullrefresh.PullRefreshIndicator
import io.github.bkmioa.nexusrss.widget.pullrefresh.pullRefresh
import io.github.bkmioa.nexusrss.widget.pullrefresh.rememberPullRefreshState

@Composable
fun ThreadList(
    requestData: RequestData,
    requestRefresh: Boolean = true,
    onRefreshed: () -> Unit = {},
    columns: Int = 0,
    visible: Boolean = true,
    keyFactory: () -> String = { "default" },
    gridState: LazyGridState = rememberLazyGridState(),
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
    LaunchedEffect(requestRefresh) {
        if (requestRefresh) {
            if (!refreshing) {
                lazyPagingItems.refresh()
            }
            onRefreshed()
        }
    }
    Box(
        Modifier
            .pullRefresh(refreshState)
            .fillMaxSize()
    ) {
        List(
            lazyPagingItems,
            gridState,
            columns = columns,
            collapsePinedItems = state.collapsePinedItems,
            onCheckCollapsePinedItems = { viewModel.setCollapsePinedItems(it) }
        )

        val refresh = lazyPagingItems.loadState.refresh
        if (refresh is LoadState.Error) {
            val context = LocalContext.current
            val message = refresh.error.message ?: "Unknown error"
            if (lazyPagingItems.itemCount == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f),
                    contentAlignment = Alignment.Center
                ) {
                    Empty(
                        image = {
                            Icon(
                                modifier = Modifier.size(100.dp),
                                imageVector = ImageVector.vectorResource(id = R.drawable.wifi_error),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                            )
                        },
                        message = message,
                        actionText = "重试"
                    ) {
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
private fun List(
    lazyPagingItems: LazyPagingItems<Item>,
    gridState: LazyGridState,
    columns: Int = 0,
    collapsePinedItems: Boolean,
    onCheckCollapsePinedItems: (Boolean) -> Unit
) {
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        var collapseItemCount = 0
        while (collapseItemCount < lazyPagingItems.itemCount) {
            val item = lazyPagingItems.peek(collapseItemCount)
            if (item?.status?.toppingLevel != 0) {
                collapseItemCount++
            } else {
                break
            }
        }

        if (collapseItemCount > 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    onClick = {
                        onCheckCollapsePinedItems(!collapsePinedItems)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "${if (collapsePinedItems) "展开" else "收起"}置顶内容",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 8.dp)
                                .rotate(if (collapsePinedItems) 90f else 270f)
                                .size(20.dp),
                            tint = LocalContentColor.current
                        )
                    }
                }
            }
        }
        if (collapsePinedItems && collapseItemCount > 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    for (index in 0 until collapseItemCount) {
                        val item = lazyPagingItems[index]
                        SmallItemCard(item, modifier = Modifier.animateItem())
                    }
                }
            }
        }

        val itemCount = if (collapsePinedItems) lazyPagingItems.itemCount - collapseItemCount else lazyPagingItems.itemCount
        val startIndex = if (collapsePinedItems) collapseItemCount else 0

        items(
            count = itemCount,
            span = { index ->
                val item = lazyPagingItems.peek(index + startIndex)
                if (item?.status?.toppingLevel != 0) {
                    GridItemSpan((maxLineSpan / 2).coerceAtLeast(2))
                } else {
                    GridItemSpan(1)
                }
            },
            key = { index -> lazyPagingItems.peek(index + startIndex)!!.id }
        ) { index ->
            val item = lazyPagingItems[index + startIndex]
            if (item?.status?.toppingLevel != 0) {
                TopItemCard(item, Modifier.animateItem())
            } else {
                ItemCard(item, aspectRatio, Modifier.animateItem())
            }
        }

        val append = lazyPagingItems.loadState.append
        if (append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "footer") {
                FooterLoading(Modifier.animateItem())
            }
        }
        if (append is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "footer") {
                Empty(
                    Modifier
                        .animateItem()
                        .height(80.dp),
                    message = append.error.message ?: "Unknown error"
                ) {
                    lazyPagingItems.retry()
                }
            }
        }
        if (append is LoadState.NotLoading && append.endOfPaginationReached && lazyPagingItems.itemCount > 0) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "footer") {
                Row(
                    Modifier.height(80.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.width(40.dp),
                        thickness = 1.dp
                    )
                    Text(
                        text = "没有更多了",
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    HorizontalDivider(
                        modifier = Modifier.width(40.dp),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
    if (lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        ) {
            Empty(
                modifier = Modifier.fillMaxSize(),
                image = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.empty),
                        contentDescription = "",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )
                },
                message = "没有内容"
            )
        }
    }
}

@Composable
private fun FooterLoading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun SmallItemCard(item: Item?, modifier: Modifier = Modifier) {
    item ?: return

    val navController = LocalNavController.current
    Card(
        modifier = modifier
            .height(80.dp)
            .aspectRatio(3 / 4f),
        shape = MaterialTheme.shapes.small,
        onClick = {
            Router.Detail.navigate(navController, item.id, item)
        },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
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
    }
}

@Composable
fun TopItemCard(item: Item?, modifier: Modifier = Modifier) {
    item ?: return

    val containerColor = when (item.status.toppingLevel) {
        1 -> MaterialTheme.colorScheme.secondaryContainer
        2 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val navController = LocalNavController.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small,
        onClick = {
            Router.Detail.navigate(navController, item.id, item)
        },
        elevation = CardDefaults.cardElevation(2.dp)
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
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                LabelBox(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    shape = MaterialTheme.shapes.small.copy(topStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
                ) {
                    SeedersAndLeechers(item)
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
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
}

@Composable
fun ItemCard(item: Item?, aspectRatio: Float = 3 / 4f, modifier: Modifier = Modifier) {
    item ?: return
    val navController = LocalNavController.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        onClick = {
            Router.Detail.navigate(navController, item.id, item)
        }, elevation = CardDefaults.cardElevation(2.dp)
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
            LabelBox(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .height(24.dp),
                shape = MaterialTheme.shapes.medium.copy(topStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
            ) {
                SeedersAndLeechers(item)
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

@Composable
private fun SeedersAndLeechers(item: Item) {
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
        Text(text = item.status.seeders)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Filled.South,
            contentDescription = "leechers"
        )
        Text(text = item.status.leechers)
    }
}

@Composable
fun LabelBox(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        shape = shape,
        shadowElevation = 1.dp
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
            content()
        }
    }
}