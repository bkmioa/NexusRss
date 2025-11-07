package io.github.bkmioa.nexusrss.util.compose

import androidx.compose.runtime.Composable

@Composable
inline fun <reified T> T.runIf(predicate: Boolean, block: @Composable T.() -> T): T = if (predicate) {
    block()
} else {
    this
}