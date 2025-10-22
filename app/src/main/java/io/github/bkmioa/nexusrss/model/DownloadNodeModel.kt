package io.github.bkmioa.nexusrss.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.bkmioa.nexusrss.download.DownloadNode
import io.github.bkmioa.nexusrss.download.QBittorrentNode
import io.github.bkmioa.nexusrss.download.TransmissionNode
import io.github.bkmioa.nexusrss.download.UTorrentNode
import kotlinx.parcelize.Parcelize

@Entity(tableName = "download_node")
@Parcelize
data class DownloadNodeModel(
    val name: String,
    val host: String,
    val userName: String,
    val password: String,
    val type: String,
    val defaultPath: String? = null,
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
) : Parcelable {
    companion object {
        const val TYPE_UTORRENT = "uTorrent"
        const val TYPE_TRANSMISSION = "Transmission"
        const val TYPE_QBITTORRENT = "qBittorrent"

        val ALL_TYPES = arrayOf(
            TYPE_QBITTORRENT,
            TYPE_TRANSMISSION,
            TYPE_UTORRENT,
        )
    }

    fun toDownloadNode(): DownloadNode {
        return when (type) {
            TYPE_UTORRENT -> UTorrentNode(host, userName, password, defaultPath)
            TYPE_TRANSMISSION -> TransmissionNode(host, userName, password, defaultPath)
            TYPE_QBITTORRENT, -> QBittorrentNode(host, userName, password, defaultPath)
            else -> throw Throwable("Unsupported type: $type")
        }
    }
}