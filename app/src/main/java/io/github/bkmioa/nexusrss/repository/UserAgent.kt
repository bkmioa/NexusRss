package io.github.bkmioa.nexusrss.repository

import android.webkit.WebSettings
import io.github.bkmioa.nexusrss.App


object UserAgent {
    val userAgentString = WebSettings.getDefaultUserAgent(App.instance)
}