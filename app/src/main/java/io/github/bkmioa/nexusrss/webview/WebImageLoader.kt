package io.github.bkmioa.nexusrss.webview

import android.net.Uri
import android.util.Log
import android.webkit.WebResourceResponse
import androidx.lifecycle.Lifecycle
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import io.github.bkmioa.nexusrss.App
import kotlinx.coroutines.runBlocking
import java.io.FileInputStream
import kotlin.math.log

object WebImageLoader {
    private const val TAG = "WebImageLoader"

    fun loadSync(url: Uri, lifecycle: Lifecycle): WebResourceResponse? = try {
        runBlocking {
            load(url, lifecycle)
        }
    } catch (e: Exception) {
        Log.e(TAG, "loadSync error", e)
        null
    }

    @OptIn(ExperimentalCoilApi::class)
    suspend fun load(url: Uri, lifecycle: Lifecycle): WebResourceResponse? {
        val request = ImageRequest.Builder(App.instance)
            .lifecycle(lifecycle)
            .data(url)
            .build()

        val imageLoader = App.instance.imageLoader
        val result = imageLoader.execute(request)
        if (result is ErrorResult) {
            Log.e(TAG, "load error", result.throwable)
            return null
        }

        result as SuccessResult

        val diskCacheKey = result.diskCacheKey ?: return null
        val diskCache = imageLoader.diskCache ?: return null
        val snapshot = diskCache.openSnapshot(diskCacheKey) ?: return null
        val cacheResponse = diskCache.fileSystem.read(snapshot.metadata) {
            CacheResponse(this)
        }
        val mimeType = cacheResponse.contentType.toString()
        val inputStream = FileInputStream(snapshot.data.toFile())
        return WebResourceResponse(mimeType, "UTF-8", inputStream)
    }
}