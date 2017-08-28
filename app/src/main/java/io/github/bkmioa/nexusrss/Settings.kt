package io.github.bkmioa.nexusrss

import com.chibatching.kotpref.KotprefModel

class Settings {
    companion object : KotprefModel() {

        override val kotprefName = BuildConfig.APPLICATION_ID + "_preferences"

        const val BASE_URL = "https://tp.m-team.cc"

        val PAGE_SIZE by intPref(20, "pageSize")
        val PASS_KEY by stringPref(key = "passkey")
        val DOWNLOAD_URL by stringPref(key = "downloadUrl")
    }

}
