package io.github.bkmioa.nexusrss.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.databinding.ActivityDetailBinding
import io.github.bkmioa.nexusrss.db.DownloadDao
import io.github.bkmioa.nexusrss.download.RemoteDownloader
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.repository.UserAgent
import kotlinx.android.synthetic.main.activity_detail.toolBar
import org.koin.android.ext.android.inject


class DetailActivity : BaseActivity() {
    companion object {
        const val KEY_TITLE = "title"
        const val KEY_SUB_TITLE = "sub_title"
        const val KEY_LINK = "link"
        const val KEY_DOWNLOAD_URL = "download_url"

        fun createIntent(
            context: Context,
            title: String? = null,
            subTitle: String? = null,
            link: String? = null,
            downloadUrl: String? = null
        ): Intent {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra(KEY_TITLE, title)
                putExtra(KEY_SUB_TITLE, subTitle)
                putExtra(KEY_LINK, link)
                putExtra(KEY_DOWNLOAD_URL, downloadUrl)
            }
            return intent
        }
    }

    private var title: String = ""
    private var subTitle: String = ""
    private var downloadUrl: String = ""
    private var link: String = ""

    private val downloadDao: DownloadDao by inject()

    private var downloadNodes: List<DownloadNodeModel> = emptyList()

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(toolBar)

        link = intent.getStringExtra(KEY_LINK) ?: handleDeepLink(intent.data) ?: ""
        title = intent.getStringExtra(KEY_TITLE) ?: ""
        subTitle = intent.getStringExtra(KEY_SUB_TITLE) ?: ""
        downloadUrl = intent.getStringExtra(KEY_DOWNLOAD_URL) ?: ""

        setTitle(title)
        subTitle(subTitle)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        binding.webView.webViewClient = InnerWebViewClient()
        binding.webView.webChromeClient = InnerWebViewChromeClient()
        binding.webView.settings.apply {
            javaScriptEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            userAgentString = UserAgent.userAgentString
        }
        loadUrl(link)

        downloadDao.getAllLiveData().observe(this) {
            downloadNodes = it
            invalidateOptionsMenu()
        }
    }

    private fun handleDeepLink(uri: Uri?): String? {
        if (uri == null) return null
        val baseUrl = uri.scheme + "://" + uri.host
        return if (baseUrl != Settings.BASE_URL) {
            uri.toString().replace(baseUrl, Settings.BASE_URL)
        } else {
            uri.toString()
        }
    }

    private fun loadUrl(link: String) {
        if (link.isNotBlank()) {
            val additionalHttpHeaders = mapOf("x-requested-with" to "WebView")
            binding.webView.loadUrl(link, additionalHttpHeaders)
        }
    }

    private fun subTitle(subTitle: String) {
        supportActionBar?.subtitle = subTitle
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (downloadNodes.isEmpty()) {
            menu.add(R.string.remote_download)
                .setOnMenuItemClickListener {
                    goSetting()
                    true
                }
        } else {
            val subMenu = menu.addSubMenu(R.string.remote_download)
            downloadNodes.forEach { node ->
                subMenu.add(node.name)
                    .setEnabled(downloadUrl.isNotBlank())
                    .setOnMenuItemClickListener {
                        downloadTo(node)
                        true
                    }
            }
        }

        menu.add(R.string.copy_link)
            .setEnabled(downloadUrl.isNotBlank())
            .setOnMenuItemClickListener {
                copyLink()
                true
            }
        menu.add(R.string.open_link)
            .setOnMenuItemClickListener {
                openLink()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    private fun openLink() {
        if (link.isNotBlank()) {
            Intent(Intent.ACTION_VIEW, Uri.parse(link))
                .run(::startActivity)
        }
    }

    private fun getTorrentUrl() = downloadUrl

    private fun copyLink() {
        val torrentUrl = getTorrentUrl()
        (getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)
            ?.apply {
                setPrimaryClip(ClipData.newPlainText(torrentUrl, torrentUrl))
                Toast.makeText(application, R.string.copy_done, Toast.LENGTH_SHORT).show()
            }
    }

    private fun downloadTo(node: DownloadNodeModel) {
        val torrentUrl = getTorrentUrl().takeIf { it.isNotBlank() } ?: return

        RemoteDownloader.download(applicationContext, node.toDownloadNode(), torrentUrl)
    }

    private fun goSetting() {
        Snackbar.make(findViewById(Window.ID_ANDROID_CONTENT), R.string.need_download_setting, Snackbar.LENGTH_LONG)
            .setAction(R.string.go_download_setting) {
                startActivity(Intent(this@DetailActivity, SettingActivity::class.java))
            }
            .show()
    }

    private inner class InnerWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (request.isForMainFrame) {
                handleUrl(request.url)
            }
            return true
        }

        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            findDownloadUrl(view)
        }
        private fun findDownloadUrl(view: WebView) {
            val getDownloadUrl = """
                (function () {
                  var aTags = document.getElementsByTagName('a');
                  for (var i = 0; i < aTags.length; i++) {
                    if (aTags[i].textContent == "[IPv4+https]") {
                      return aTags[i].href;
                    }
                  }
                })();
            """
            view.evaluateJavascript(getDownloadUrl) { value ->
                downloadUrl = value.trim('"')
                invalidateOptionsMenu()
            }
        }


        private fun handleUrl(uri: Uri) {
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if (uri.path?.startsWith("/details.php") == true) {
                if (uri.host?.endsWith("m-team.cc") == true) {
                    val baseUrl = uri.scheme + "://" + uri.host

                    if (baseUrl != Settings.BASE_URL) {
                        val newUrl = uri.toString().replace(baseUrl, Settings.BASE_URL)
                        intent.data = newUrl.toUri()
                    }
                    intent.setPackage(packageName)
                }
            }

            startActivity(intent)
        }
    }

    inner class InnerWebViewChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String?) {
            super.onReceivedTitle(view, title)
            if (this@DetailActivity.title.isBlank()) {
                setTitle(extractTitle(title))
            }
        }
    }

    private fun extractTitle(title: String?): String {
        if (title.isNullOrBlank()) return ""
        val first = title.indexOfFirst { it == '"' }
        val last = title.indexOfLast { it == '"' }
        if (first == -1 || last == -1) return title
        return title.substring(first + 1, last)
    }
}
