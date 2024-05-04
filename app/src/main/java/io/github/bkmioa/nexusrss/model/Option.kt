package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(val path: String, val des: String, val options: List<Option> = emptyList()) : Parcelable {
    companion object {
        val NORMAL = Category("normal", "Normal", Option.ALL)
        val MOVIE = Category("movie", "Movie", Option.MOVIES)
        val TV = Category("tvshow", "TV", Option.TV)
        val MUSIC = Category("music", "Music", Option.MUSIC)
        val ADULT = Category("adult", "Adult", Option.ADULT)

        val ALL_CATEGORY = listOf(NORMAL, MOVIE, TV, MUSIC, ADULT)
    }

}

@Parcelize
data class Option(
    val key: String,
    val value: String,
    val des: String,
    var img: String? = null
) : Parcelable {

    @IgnoredOnParcel
    val id = "${key}_${value}"

    companion object {
        val MOVIES = listOf(
            Option("cat", "401", "電影/SD", "/static/cate/moviesd.png"),
            Option("cat", "419", "電影/HD", "/static/cate/moviehd.png"),
            Option("cat", "420", "電影/DVDiSo", "/static/cate/moviedvd.png"),
            Option("cat", "421", "電影/Blu-Ray", "/static/cate/moviebd.png"),
            Option("cat", "439", "電影/Remux", "/static/cate/movieremux.png"),
            Option("cat", "404", "紀錄教育", "/static/cate/bbc.png"),
        )

        val TV = listOf(
            Option("cat", "402", "影劇/綜藝/HD", "/static/cate/tvhd.png"),
            Option("cat", "403", "影劇/綜藝/SD", "/static/cate/tvsd.png"),
            Option("cat", "435", "影劇/綜藝/DVDiSo", "/static/cate/tvdvd.png"),
            Option("cat", "438", "影劇/綜藝/BD", "/static/cate/tvbd.png"),
        )

        val ADULT = listOf(
            Option("cat", "410", "AV(有碼)/HD Censored", "/static/cate/cenhd.png"),
            Option("cat", "424", "AV(有碼)/SD Censored", "/static/cate/censd.png"),
            Option("cat", "431", "AV(有碼)/Blu-Ray Censored", "/static/cate/cenbd.png"),
            Option("cat", "437", "AV(有碼)/DVDiSo Censored", "/static/cate/cendvd.png"),
            Option("cat", "426", "AV(無碼)/DVDiSo Uncensored", "/static/cate/uendvd.png"),
            Option("cat", "429", "AV(無碼)/HD Uncensored", "/static/cate/uenhd.png"),
            Option("cat", "430", "AV(無碼)/SD Uncensored", "/static/cate/uensd.png"),
            Option("cat", "432", "AV(無碼)/Blu-Ray Uncensored", "/static/cate/uenbd.png"),
            Option("cat", "436", "AV(網站)/0Day", "/static/cate/adult0day.png"),
            Option("cat", "440", "AV(Gay)/HD", "/static/cate/gayhd.gif"),
            Option("cat", "411", "H-遊戲", "/static/cate/hgame.png"),
            Option("cat", "412", "H-動畫", "/static/cate/hanime.png"),
            Option("cat", "413", "H-漫畫", "/static/cate/hcomic.png"),
            Option("cat", "425", "IV(寫真影集)", "/static/cate/ivvideo.png"),
            Option("cat", "433", "IV(寫真圖集)", "/static/cate/ivpic.png"),
        )
        val MUSIC = listOf(
            Option("cat", "408", "Music(AAC/ALAC)", "/static/cate/mp3.png"),
            Option("cat", "434", "Music(無損)", "/static/cate/flac.png"),
            Option("cat", "406", "演唱", "/static/cate/mv.png"),
        )

        val OTHERS = listOf(
            Option("cat", "405", "動畫", "/static/cate/anime.png"),
            Option("cat", "407", "運動", "/static/cate/sport.png"),
            Option("cat", "409", "Misc(其他)", "/static/cate/other.png"),
            Option("cat", "422", "軟體", "/static/cate/software.png"),
            Option("cat", "423", "PC遊戲", "/static/cate/pcgame.png"),
            Option("cat", "427", "電子書", "/static/cate/ebook.png"),
        )

        val ALL = MOVIES + TV + MUSIC + OTHERS

        @Deprecated("Use STANDARDS instead")
        val RESOLUTION = listOf(
            Option("standard", "1", "1080p"),
            Option("standard", "2", "1080i"),
            Option("standard", "3", "720p"),
            Option("standard", "5", "SD"),
            Option("standard", "6", "4K"),
        )

        val STANDARDS = RESOLUTION

        @Deprecated("Use PROCESSINGS instead")
        val PROCESS = listOf(
            Option("processing", "1", "陆剧"),
            Option("processing", "2", "美剧"),
            Option("processing", "3", "港台剧"),
            Option("processing", "4", "日剧"),
            Option("processing", "5", "韩剧"),
            Option("processing", "6", "纪录片")
        )

        val PROCESSINGS = PROCESS

        val VIDEOCODECS = listOf(
            Option("videoCodec", "1", "H.264"),
            Option("videoCodec", "2", "VC-1"),
            Option("videoCodec", "3", "Xvid"),
            Option("videoCodec", "4", "MPEG-2"),
            Option("videoCodec", "15", "MPEG-4"),
            Option("videoCodec", "16", "H.265"),
            Option("videoCodec", "19", "AV1"),
        )

        val AUDIOCODECS = listOf(
            Option("audioCodec", "1", "FLAC"),
            Option("audioCodec", "2", "APE"),
            Option("audioCodec", "3", "DTS"),
            Option("audioCodec", "4", "MP3"),
            Option("audioCodec", "5", "OGG"),
            Option("audioCodec", "6", "AAC"),
            Option("audioCodec", "7", "Other"),
        )

        val TEAMS = listOf(
            Option("team", "6", "BMDru"),
            Option("team", "7", "KiSHD"),
            Option("team", "8", "Pack"),
            Option("team", "9", "MTeam"),
            Option("team", "10", "MPAD"),
            Option("team", "17", "MTeamTV"),
            Option("team", "18", "OneHD"),
            Option("team", "19", "CNHK"),
            Option("team", "20", "StBOX"),
            Option("team", "21", "R2HD"),
            Option("team", "22", "LowPower-Raws"),
            Option("team", "23", "TnP"),
            Option("team", "24", "Geek"),
            Option("team", "25", "CatEDU"),
        )

        val LABELS = listOf(
            Option("label", "diy", "DIY"),
            Option("label", "dub", "国配"),
            Option("label", "sub", "中字"),
        )

        val DISCOUNTS = listOf(
            Option("discount", "", "全部"),
            Option("discount", "NORMAL", "普通"),
            Option("discount", "PERCENT_70", "30%"),
            Option("discount", "PERCENT_50", "50%"),
            Option("discount", "FREE", "免費"),
        )
    }
}
