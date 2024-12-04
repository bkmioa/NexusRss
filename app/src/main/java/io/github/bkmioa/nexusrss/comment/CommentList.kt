package io.github.bkmioa.nexusrss.comment

import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.airbnb.mvrx.compose.mavericksViewModel
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.model.Comment
import io.github.bkmioa.nexusrss.widget.Empty

@Composable
fun CommentList(relationId: String, scope: LifecycleOwner) {
    val viewModel: CommentViewModel = mavericksViewModel(scope, argsFactory = { relationId })

    val lazyPagingItems = viewModel.pagerFlow.collectAsLazyPagingItems()
    val refresh = lazyPagingItems.loadState.refresh

    when {
        refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
            ) {
                Empty(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    image = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.empty),
                            contentDescription = "",
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                    },
                    message = "暂无评论"
                )
            }
        }

        refresh is LoadState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Empty(message = refresh.error.message ?: "Unknown error") {
                    lazyPagingItems.retry()
                }
            }
        }

        refresh is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            LazyColumn() {
                items(lazyPagingItems.itemCount) {
                    CommentItem(comment = lazyPagingItems.get(it)!!)
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            AsyncImage(
                model = comment.member?.avatarUrl,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = painterResource(R.drawable.ic_default_avatar),
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = comment.member?.username ?: comment.author,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        comment.getDateText(),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
