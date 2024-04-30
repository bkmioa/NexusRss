package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(val path: String, val des: String, val options: List<Option> = emptyList()) : Parcelable {
    companion object {
        val NORMAL = Category("normal", "Normal", Option.ALL)
        val MOVIE = Category("movie", "Movie", Option.MOVIES)
        val TV = Category("tvshow", "TV", Option.MOVIES)
        val MUSIC = Category("music", "Music", Option.MUSIC)
        val ADULT = Category("adult", "Adult", Option.ADULT)

        val ALL_CATEGORY = listOf(NORMAL, MOVIE, MUSIC, ADULT)
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
            Option("cat_401", "電影/SD", "/static/cate/moviesd.png"),
            Option("cat_419", "電影/HD", "/static/cate/moviehd.png"),
            Option("cat_420", "電影/DVDiSo", "/static/cate/moviedvd.png"),
            Option("cat_421", "電影/Blu-Ray", "/static/cate/moviebd.png"),
            Option("cat_439", "電影/Remux", "/static/cate/movieremux.png"),
            Option("cat_404", "紀錄教育", "/static/cate/bbc.png"),
        )

        val TV = listOf(
            Option("cat_402", "影劇/綜藝/HD", "/static/cate/tvhd.png"),
            Option("cat_403", "影劇/綜藝/SD", "/static/cate/tvsd.png"),
            Option("cat_435", "影劇/綜藝/DVDiSo", "/static/cate/tvdvd.png"),
            Option("cat_438", "影劇/綜藝/BD", "/static/cate/tvbd.png"),
        )

        val ADULT = listOf(
            Option("cat_410", "AV(有碼)/HD Censored", "/static/cate/cenhd.png"),
            Option("cat_424", "AV(有碼)/SD Censored", "/static/cate/censd.png"),
            Option("cat_431", "AV(有碼)/Blu-Ray Censored", "/static/cate/cenbd.png"),
            Option("cat_437", "AV(有碼)/DVDiSo Censored", "/static/cate/cendvd.png"),
            Option("cat_426", "AV(無碼)/DVDiSo Uncensored", "/static/cate/uendvd.png"),
            Option("cat_429", "AV(無碼)/HD Uncensored", "/static/cate/uenhd.png"),
            Option("cat_430", "AV(無碼)/SD Uncensored", "/static/cate/uensd.png"),
            Option("cat_432", "AV(無碼)/Blu-Ray Uncensored", "/static/cate/uenbd.png"),
            Option("cat_436", "AV(網站)/0Day", "/static/cate/adult0day.png"),
            Option("cat_440", "AV(Gay)/HD", "/static/cate/gayhd.gif"),
            Option("cat_411", "H-遊戲", "/static/cate/hgame.png"),
            Option("cat_412", "H-動畫", "/static/cate/hanime.png"),
            Option("cat_413", "H-漫畫", "/static/cate/hcomic.png"),
            Option("cat_425", "IV(寫真影集)", "/static/cate/ivvideo.png"),
            Option("cat_433", "IV(寫真圖集)", "/static/cate/ivpic.png"),
        )
        val MUSIC = listOf(
            Option("cat_408", "Music(AAC/ALAC)", "/static/cate/mp3.png"),
            Option("cat_434", "Music(無損)", "/static/cate/flac.png"),
            Option("cat_406", "演唱", "/static/cate/mv.png"),
        )

        val OTHERS = listOf(
            Option("cat_405", "動畫", "/static/cate/anime.png"),
            Option("cat_407", "運動", "/static/cate/sport.png"),
            Option("cat_409", "Misc(其他)", "/static/cate/other.png"),
            Option("cat_422", "軟體", "/static/cate/software.png"),
            Option("cat_423", "PC遊戲", "/static/cate/pcgame.png"),
            Option("cat_427", "電子書", "/static/cate/ebook.png"),
        )

        val ALL = MOVIES + TV + MUSIC + OTHERS

        val RESOLUTION = listOf(
            Option("standard_1", "1080p"),
            Option("standard_2", "1080i"),
            Option("standard_3", "720p"),
            Option("standard_5", "SD"),
            Option("standard_6", "4K"),
        )

        val PROCESS = listOf(
            Option("processing_1", "陆剧"),
            Option("processing_2", "美剧"),
            Option("processing_3", "港台剧"),
            Option("processing_4", "日剧"),
            Option("processing_5", "韩剧"),
            Option("processing_6", "纪录片")
        )
    }
}
