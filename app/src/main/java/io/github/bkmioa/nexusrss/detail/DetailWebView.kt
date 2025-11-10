package io.github.bkmioa.nexusrss.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavOptions
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.html.Html
import io.github.bkmioa.nexusrss.repository.UserAgent
import io.github.bkmioa.nexusrss.webview.WebImageLoader
import io.github.bkmioa.nexusrss.widget.AccompanistWebViewClient
import io.github.bkmioa.nexusrss.widget.WebView
import io.github.bkmioa.nexusrss.widget.rememberWebViewStateWithHTMLData

@Composable
fun DetailWebView(data: String?) {
    val webViewState = rememberWebViewStateWithHTMLData(
        data = Html.render(data),
        baseUrl = Settings.BASE_URL,
        encoding = "utf-8",
        mimeType = "text/html",
        historyUrl = null
    )
    val backgroundColor = MaterialTheme.colorScheme.background.value.toInt()
    val controller = LocalNavController.current
    WebView(
        state = webViewState,
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT),
        client = InnerWebViewClient(lifecycleOwner = LocalLifecycleOwner.current, {
            controller.navigate(it, NavOptions.Builder().setRestoreState(true).build())
        }),
        captureBackPresses = false,
        onCreated = {
            it.isVerticalScrollBarEnabled = false
            //https://stackoverflow.com/a/75076174
            it.alpha = 0.99f
            it.setBackgroundColor(backgroundColor)
            it.settings.apply {
                javaScriptEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                useWideViewPort = false
                displayZoomControls = false
                userAgentString = UserAgent.userAgentString
            }
        },
        onDispose = {
            it.destroy()
        },

        )

    //AndroidView(
    //    factory = { context ->
    //        android.webkit.WebView(context).apply {
    //            webViewClient = InnerWebViewClient(lifecycleOwner)
    //            settings.apply {
    //                javaScriptEnabled = true
    //                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    //                useWideViewPort = false
    //                displayZoomControls = false
    //                userAgentString = UserAgent.userAgentString
    //            }
    //        }
    //    },
    //    update = { webView ->
    //        if (data != null) {
    //            webView.loadDataWithBaseURL(Settings.BASE_URL, Html.render(data), "text/html", "utf-8", null)
    //        }
    //    }
    //)
}

private class InnerWebViewClient(val lifecycleOwner: LifecycleOwner, val onRequestOpenDeepLink: (Uri) -> Unit) : AccompanistWebViewClient() {
    override fun shouldOverrideUrlLoading(view: android.webkit.WebView, request: WebResourceRequest): Boolean {
        if (request.isForMainFrame) {
            handleUrl(view.context, request.url)
        }
        return true
    }

    override fun shouldInterceptRequest(view: android.webkit.WebView, request: WebResourceRequest): WebResourceResponse? {
        //拦截图片请求
        val url = request.url.toString()
        if (url.endsWith("jpg", true)
            || url.endsWith("png", true)
            || url.endsWith("gif", true)
            || url.endsWith("jpeg", true)
            || url.endsWith("webp", true)
            || url.endsWith("bmp", true)
            || url.endsWith("svg", true)
        ) {
            return WebImageLoader.loadSync(request.url, lifecycleOwner.lifecycle)
        }
        return super.shouldInterceptRequest(view, request)
    }

    private fun handleUrl(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (uri.host?.endsWith("m-team.cc") == true && uri.path?.startsWith("/detail") == true) {
            onRequestOpenDeepLink(uri)
            return
        }

        context.startActivity(intent)
    }
}