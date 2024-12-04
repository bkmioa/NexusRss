package io.github.bkmioa.nexusrss.model

import androidx.annotation.IntRange


data class CommentRequestBody(
    val relationId: String,

    val type: String = "TORRENT",

    @IntRange(from = 1)
    val pageNumber: Int,

    val pageSize: Int = 20,
)