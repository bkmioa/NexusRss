package io.github.bkmioa.nexusrss

import android.content.Context

fun Context.dp2px(dp: Number) = (resources.displayMetrics.density * dp.toFloat() + 0.5f).toInt()
class Util {

}