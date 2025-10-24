package io.github.bkmioa.nexusrss.widget

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

fun CharSequence?.asToastMessage(duration: Int = Toast.LENGTH_SHORT): ToastMessage {
    return ToastMessage(this, duration)
}

class ToastMessage(
    val text: CharSequence? = null,
    val resId: Int? = null,
    val duration: Int = Toast.LENGTH_SHORT
) {
    fun isEmpty(): Boolean {
        return text == null && (resId == null || resId == 0)
    }
}

@Composable
fun Toaster(toast: ToastMessage?) {
    toast ?: return

    if (toast.isEmpty()) return

    val context = LocalContext.current
    LaunchedEffect(toast) {
        if (toast.resId != null && toast.resId != 0) {
            Toast.makeText(context, toast.resId, toast.duration).show()
        } else {
            Toast.makeText(context, toast.text, toast.duration).show()
        }
    }
}