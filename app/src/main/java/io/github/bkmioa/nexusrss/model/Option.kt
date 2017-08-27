package io.github.bkmioa.nexusrss.model

import android.os.Parcel
import android.os.Parcelable
import io.github.bkmioa.nexusrss.Settings

class Option constructor(val key: String, val des: String, var img: String? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(key)
        writeString(des)
        writeString(img)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Option

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    companion object {
        val CATEGORY = arrayOf(
                Option("cat401", "Movie(電影)/SD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviesd.png"),
                Option("cat419", "Movie(電影)/HD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviehd.png"),
                Option("cat420", "Movie(電影)/DVDiSo", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviedvd.png"),
                Option("cat421", "Movie(電影)/Blu-Ray", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/moviebd.png"),
                Option("cat439", "Movie(電影)/Remux", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/movieremux.png"),
                Option("cat403", "TV Series(影劇/綜藝)/SD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvsd.png"),
                Option("cat402", "TV Series(影劇/綜藝)/HD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvhd.png"),
                Option("cat435", "TV Series(影劇/綜藝)/DVDiSo", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvdvd.png"),
                Option("cat438", "TV Series(影劇/綜藝)/BD", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/tvbd.png"),
                Option("cat404", "紀錄教育", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/bbc.png"),
                Option("cat405", "Anime(動畫)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/anime.png"),
                Option("cat406", "MV(演唱)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/mv.png"),
                Option("cat408", "Music(AAC/ALAC)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/mp3.png"),
                Option("cat434", "Music(無損)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/flac.png"),
                Option("cat407", "Sports(運動)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/sport.png"),
                Option("cat422", "Software(軟體)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/software.png"),
                Option("cat423", "PCGame(PC遊戲)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/pcgame.png"),
                Option("cat427", "eBook(電子書)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/ebook.png"),
                Option("cat410", "AV(有碼)/HD Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/cenhd.png"),
                Option("cat429", "AV(無碼)/HD Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uenhd.png"),
                Option("cat424", "AV(有碼)/SD Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/censd.png"),
                Option("cat430", "AV(無碼)/SD Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uensd.png"),
                Option("cat426", "AV(無碼)/DVDiSo Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uendvd.png"),
                Option("cat437", "AV(有碼)/DVDiSo Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/cendvd.png"),
                Option("cat431", "AV(有碼)/Blu-Ray Censored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/cenbd.png"),
                Option("cat432", "AV(無碼)/Blu-Ray Uncensored", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/uenbd.png"),
                Option("cat436", "AV(網站)/0Day", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/adult0day.png"),
                Option("cat425", "IV(寫真影集)/Video Collection", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/ivvideo.png"),
                Option("cat433", "IV(寫真圖集)/Picture Collection", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/ivpic.png"),
                Option("cat411", "H-Game(遊戲)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/hgame.png"),
                Option("cat412", "H-Anime(動畫)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/hanime.png"),
                Option("cat413", "H-Comic(漫畫)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/hcomic.png"),
                Option("cat409", "Misc(其他)", Settings.BASE_URL + "/pic/category/chd/scenetorrents/cht/other.png")
        )

        val CODE = arrayOf(
                Option("cod1", "H.264"),
                Option("cod2", "VC-1"),
                Option("cod3", "Xvid"),
                Option("cod4", "MPEG-2"),
                Option("cod5", "FLAC"),
                Option("cod10", "APE"),
                Option("cod11", "DTS"),
                Option("cod12", "AC-3"),
                Option("cod13", "WAV"),
                Option("cod14", "MP3"),
                Option("cod15", "MPEG-4"),
                Option("cod16", "H.265"),
                Option("cod17", "ALAC"),
                Option("cod18", "AAC")
        )

        val RESOLUTION = arrayOf(
                Option("sta1", "1080p"),
                Option("sta2", "1080i"),
                Option("sta3", "720p"),
                Option("sta5", "SD"),
                Option("sta6", "4K")
        )

        val PROCESS = arrayOf(
                Option("pro1", "CN"),
                Option("pro2", "US/EU"),
                Option("pro3", "HK/TW"),
                Option("pro4", "JP"),
                Option("pro5", "KR"),
                Option("pro6", "OT")
        )

        val TEAM = arrayOf(
                Option("tea9", "MTeam"),
                Option("tea10", "MTeamPAD"),
                Option("tea16", "MTeam3D"),
                Option("tea17", "MTeamTV"),
                Option("tea7", "KiSHD"),
                Option("tea6", "BMDru"),
                Option("tea18", "OneHD"),
                Option("tea19", "CNHK"),
                Option("tea20", "StBOX"),
                Option("tea21", "R2HD"),
                Option("tea8", "Pack")
        )

        @JvmField
        val CREATOR: Parcelable.Creator<Option> = object : Parcelable.Creator<Option> {
            override fun createFromParcel(source: Parcel): Option = Option(source)
            override fun newArray(size: Int): Array<Option?> = arrayOfNulls(size)
        }
    }

}
