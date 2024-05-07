package io.github.bkmioa.nexusrss

import com.chibatching.kotpref.KotprefModel

class Settings {
    companion object : KotprefModel() {

        override val kotprefName = BuildConfig.APPLICATION_ID + "_preferences"

        const val DEFAULT_BASE_URL = "https://kp.m-team.cc"

        var BASE_URL by stringPref(DEFAULT_BASE_URL, key = "baseUrl")
        val LOGIN_URL
            get() = "$BASE_URL/login"
        var API_KEY by stringPref(key = "apiKey")
        var PAGE_SIZE by intPref(50, "pageSize")
        var REMOTE_URL by stringPref("http://localhost", key = "remoteUrl")
        var REMOTE_USERNAME by stringPref(key = "remoteUsername")
        var REMOTE_PASSWORD by stringPref(key = "remotePassword")
    }
}
