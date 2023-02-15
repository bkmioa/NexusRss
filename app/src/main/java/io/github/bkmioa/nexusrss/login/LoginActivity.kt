package io.github.bkmioa.nexusrss.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBar
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.databinding.ActivityLoginBinding
import io.github.bkmioa.nexusrss.repository.UserAgent
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    companion object {
        private const val TAG = "LoginActivity"

        private val initScript = """
         javascript:(
             function()
             {
                document.body.style.minWidth = "0px"
             })()
        """

        private val getInfoBlock = """
                (function() { return (document.getElementById('info_block').innerHTML); })();
        """

        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private lateinit var binding: ActivityLoginBinding

    private var verifysuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        binding.refreshLayout.setOnRefreshListener {
            binding.webView.reload()
        }

        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        WebView.setWebContentsDebuggingEnabled(true);
        binding.webView.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = false
            builtInZoomControls = false
            userAgentString = UserAgent.userAgentString
        }
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.refreshLayout.isRefreshing = false

                webView.loadUrl(initScript)
                webView.evaluateJavascript(getInfoBlock) {
                    val content = it ?: return@evaluateJavascript
                    if (content.contains("logout.php")) {
                        verifysuccess = true
                        finish()
                    }
                }
            }
        }
        val additionalHttpHeaders = mapOf("x-requested-with" to "WebView")
        binding.webView.loadUrl(Settings.LOGIN_URL, additionalHttpHeaders)
    }

    override fun finish() {
        super.finish()
        if (verifysuccess) {
            VerifyManager.notifyVerifySuccess()
        } else {
            VerifyManager.notifyVerifyFailure()
        }
    }
}