package io.github.bkmioa.nexusrss.detail

import android.os.Parcelable
import io.github.bkmioa.nexusrss.model.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailArgs(
    val id: String,
    val data: Item?
) : Parcelable
