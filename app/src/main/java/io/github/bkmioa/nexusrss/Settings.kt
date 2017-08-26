package io.github.bkmioa.nexusrss

import com.chibatching.kotpref.KotprefModel
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab

class Settings {
    companion object : KotprefModel() {
        override val kotprefName = BuildConfig.APPLICATION_ID + "_preferences"

        const val BASE_URL = "https://tp.m-team.cc"

        private val TAB_MOVIE = Tab("MOVIE",
                arrayOf(
                        Option.CATEGORY[0],
                        Option.CATEGORY[1],
                        Option.CATEGORY[2],
                        Option.CATEGORY[3],
                        Option.CATEGORY[4]
                ), 0)
        private val TAB_TV = Tab("TV",
                arrayOf(
                        Option.CATEGORY[5],
                        Option.CATEGORY[6],
                        Option.CATEGORY[7],
                        Option.CATEGORY[8]
                ), 1)
        private val TAB_ANIME = Tab("ANIME",
                arrayOf(
                        Option.CATEGORY[10]
                ), 2)
        private val TAB_MUSIC = Tab("MUSIC",
                arrayOf(
                        Option.CATEGORY[11],
                        Option.CATEGORY[12],
                        Option.CATEGORY[13]
                ), 3)
        val PAGE_SIZE by intPref(20, "pageSize")

        val tabs = arrayListOf(TAB_MOVIE, TAB_TV, TAB_ANIME, TAB_MUSIC)

        val PASS_KEY by stringPref(key = "passkey")
        val DOWNLOAD_URL by stringPref(key = "downloadUrl")
    }
}