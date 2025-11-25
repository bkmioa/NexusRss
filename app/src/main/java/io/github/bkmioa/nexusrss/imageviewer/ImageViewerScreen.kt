@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, InternalCoilApi::class)

package io.github.bkmioa.nexusrss.imageviewer

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.URLUtil
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.annotation.InternalCoilApi
import coil3.imageLoader
import coil3.network.CacheNetworkResponse
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import com.github.panpf.zoomimage.rememberCoilZoomState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.SharedTransitionDataWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.IOException
import kotlin.math.max

@Destination<RootGraph>(
    wrappers = [SharedTransitionDataWrapper::class]
)
@Composable
fun ImageViewerScreen(
    navigator: DestinationsNavigator,
    images: Array<String>,
    initIndex: Int = 0
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val imageCount = images.size
    val hasImages = imageCount > 0
    val initialPage = remember(images, initIndex) {
        if (!hasImages) 0 else initIndex.coerceIn(0, imageCount - 1)
    }

    var topBarVisible by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { max(imageCount, 1) }
    )

    val saveSuccessMessage = stringResource(id = R.string.save_image_success)
    val saveFailureMessage = stringResource(id = R.string.save_image_failure)


    Scaffold(
        containerColor = Color.Black,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = topBarVisible,
                enter = slideInVertically(animationSpec = tween(), initialOffsetY = { -it }),
                exit = slideOutVertically(animationSpec = tween(), targetOffsetY = { -it })
            ) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navigator.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = hasImages && !saving,
                            onClick = {
                                if (!hasImages || saving) {
                                    return@IconButton
                                }
                                val targetUrl = images[pagerState.currentPage]
                                coroutineScope.launch {
                                    saving = true
                                    val success = saveImageToGallery(context, targetUrl)
                                    saving = false
                                    val message = if (success) {
                                        saveSuccessMessage
                                    } else {
                                        saveFailureMessage
                                    }
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = stringResource(id = R.string.action_save_image)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(0.5f),
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = hasImages && imageCount > 1,
                key = { index -> if (hasImages) images[index] else "placeholder" },
                modifier = Modifier.fillMaxSize()
            ) { page ->
                if (!hasImages) {
                    return@HorizontalPager
                }
                var loading by remember { mutableStateOf(true) }
                val imageUrl = images[page]
                val zoomState = rememberCoilZoomState()
                val imageRequest = remember(imageUrl, context) {
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build()
                }

                Box(contentAlignment = Alignment.Center) {
                    CoilZoomAsyncImage(
                        model = imageRequest,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        zoomState = zoomState,
                        onLoading = {
                            loading = true
                        },
                        onSuccess = {
                            loading = false
                        },
                        onError = {
                            loading = false
                        },
                        onTap = {
                            topBarVisible = !topBarVisible
                        }
                    )
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            if (!hasImages) {
                Text(
                    text = stringResource(id = R.string.no_images_to_preview),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "${pagerState.currentPage + 1}/$imageCount",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private suspend fun saveImageToGallery(context: Context, imageUrl: String): Boolean = withContext(Dispatchers.IO) {
    val imageLoader = context.imageLoader
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()
    val result = imageLoader.execute(request)
    if (result !is SuccessResult) return@withContext false

    val diskCacheKey = result.diskCacheKey ?: return@withContext false
    val diskCache = imageLoader.diskCache ?: return@withContext false
    val snapshot = diskCache.openSnapshot(diskCacheKey) ?: return@withContext false

    snapshot.use { snap ->
        val cacheResponse = diskCache.fileSystem.read(snap.metadata) {
            CacheNetworkResponse.readFrom(this)
        }
        val mimeType = cacheResponse.headers["Content-Type"] ?: DEFAULT_MIME_TYPE
        val displayName = URLUtil.guessFileName(imageUrl, null, mimeType)
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, IMAGE_RELATIVE_PATH)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(collection, values) ?: return@withContext false
        return@withContext try {
            resolver.openOutputStream(uri)?.use { output ->
                FileInputStream(snap.data.toFile()).use { input ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Unable to open MediaStore output stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }.also { resolver.update(uri, it, null, null) }
            }
            true
        } catch (ioe: IOException) {
            resolver.delete(uri, null, null)
            false
        }
    }
}


private val IMAGE_RELATIVE_PATH = Environment.DIRECTORY_PICTURES + "/MT"
private const val DEFAULT_MIME_TYPE = "image/jpeg"
