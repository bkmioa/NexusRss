package io.github.bkmioa.nexusrss.detail

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavOptions
import io.github.bkmioa.nexusrss.LocalNavController
import io.github.bkmioa.nexusrss.bbcode.BbCodeContent
import io.github.bkmioa.nexusrss.util.convertMarkdown2BBCode

@Composable
fun RichContent(data: String?) {
    if (data.isNullOrBlank()) return

    val controller = LocalNavController.current
    val navOptions = remember { NavOptions.Builder().setRestoreState(true).build() }
    val handleLink: (String) -> Boolean = remember(controller, navOptions) {
        { target ->
            val uri = runCatching { Uri.parse(target) }.getOrNull() ?: return@remember false
            if (uri.host?.endsWith("m-team.cc") == true && uri.path?.startsWith("/detail") == true) {
                controller.navigate(uri, navOptions)
                true
            } else {
                false
            }
        }
    }
    BbCodeContent(
        text = remember { convertMarkdown2BBCode(data) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        onLinkClick = handleLink
    )
}