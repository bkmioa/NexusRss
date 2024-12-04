package io.github.bkmioa.nexusrss.model

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Comment(
    val id: String,
    val createdDate: String,
    val author: String,
    val text: String,
) {
    @Transient
    var member: MemberInfo? = null

    fun getDateText(): String {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(createdDate)
            return DateUtils.getRelativeTimeSpanString(date.time).toString()
        } catch (e: Exception) {
            return ""
        }
    }
}
