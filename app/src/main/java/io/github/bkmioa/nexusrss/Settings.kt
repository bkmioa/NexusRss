package io.github.bkmioa.nexusrss

import com.chibatching.kotpref.KotprefModel

class Settings {
    companion object : KotprefModel() {

        override val kotprefName = BuildConfig.APPLICATION_ID + "_preferences"

        const val BASE_URL = "https://tp.m-team.cc"

        var PAGE_SIZE by intPref(20, "pageSize")
        var PASS_KEY by stringPref(key = "passkey")
        var REMOTE_URL by stringPref("http://localhost", key = "remoteUrl")
        var REMOTE_USERNAME by stringPref(key = "remoteUsername")
        var REMOTE_PASSWORD by stringPref(key = "remotePassword")
    }
}
