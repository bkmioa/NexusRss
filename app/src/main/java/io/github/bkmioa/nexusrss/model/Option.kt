package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import io.github.bkmioa.nexusrss.Settings
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(val path: String, val des: String, val options: List<Option> = emptyList()) : Parcelable {
    companion object {
        val ALL = Category("torrents.php", "ALL", Option.ALL)
        val MOVIE = Category("movie.php", "Movie", Option.MOVIES)
        val MUSIC = Category("music.php", "Music")
        val ADULT = Category("adult.php", "Adult", Option.ADULT)

        val ALL_CATEGORY = listOf(ALL, MOVIE, MUSIC, ADULT)
    }

}

@Parcelize
data class Option constructor(
    val key: String,
    val des: String,
    var img: String? = null
) : Parcelable {

    companion object {
        val MOVIES = listOf(
            Option("cat401", "Movie(電影)/SD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviesd.png"),
            Option("cat419", "Movie(電影)/HD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviehd.png"),
            Option("cat420", "Movie(電影)/DVDiSo", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviedvd.png"),
            Option("cat421", "Movie(電影)/Blu-Ray", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviebd.png"),
            Option("cat439", "Movie(電影)/Remux", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/movieremux.png"),
        )

        val TV = listOf(
            Option("cat402", "TV Series(影劇/綜藝)/HD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvhd.png"),
            Option("cat403", "TV Series(影劇/綜藝)/SD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvsd.png"),
            Option("cat435", "TV Series(影劇/綜藝)/DVDiSo", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvdvd.png"),
            Option("cat438", "TV Series(影劇/綜藝)/BD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvbd.png"),
        )

        val ADULT = listOf(
            Option("cat410", "AV(有碼)/HD Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/cenhd.png"),
            Option("cat429", "AV(無碼)/HD Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uenhd.png"),
            Option("cat424", "AV(有碼)/SD Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/censd.png"),
            Option("cat430", "AV(無碼)/SD Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uensd.png"),
            Option("cat437", "AV(有碼)/DVDiSo Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/cendvd.png"),
            Option("cat426", "AV(無碼)/DVDiSo Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uendvd.png"),
            Option("cat431", "AV(有碼)/Blu-Ray Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/cenbd.png"),
            Option("cat432", "AV(無碼)/Blu-Ray Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uenbd.png"),
            Option("cat436", "AV(網站)/0Day", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/adult0day.png"),
            Option("cat425", "IV(寫真影集)/Video Collection", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/ivvideo.png"),
            Option("cat433", "IV(寫真圖集)/Picture Collection", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/ivpic.png"),
            Option("cat411", "H-Game(遊戲)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/hgame.png"),
            Option("cat412", "H-Anime(動畫)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/hanime.png"),
            Option("cat413", "H-Comic(漫畫)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/hcomic.png"),
        )

        val ALL = MOVIES + TV + listOf(
            Option("cat404", "紀錄教育", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/bbc.png"),
            Option("cat405", "Anime(動畫)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/anime.png"),
            Option("cat406", "MV(演唱)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/mv.png"),
            Option("cat408", "Music(AAC/ALAC)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/mp3.png"),
            Option("cat434", "Music(無損)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/flac.png"),
            Option("cat407", "Sports(運動)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/sport.png"),
            Option("cat422", "Software(軟體)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/software.png"),
            Option("cat423", "PCGame(PC遊戲)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/pcgame.png"),
            Option("cat427", "eBook(電子書)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/ebook.png"),
            Option("cat409", "Misc(其他)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/other.png")
        )

        val RESOLUTION = listOf(
            Option("standard1", "1080p"),
            Option("standard6", "4K")
        )

        val PROCESS = listOf(
            Option("processing1", "陆剧"),
            Option("processing2", "美剧"),
            Option("processing3", "港台剧"),
            Option("processing4", "日剧"),
            Option("processing5", "韩剧"),
            Option("processing6", "纪录片")
        )
    }
}
