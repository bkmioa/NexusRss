package io.github.bkmioa.nexusrss.util

inline fun <reified T> T.runIf(predicate: Boolean, block: T.() -> T): T = if (predicate) {
    block()
} else {
    this
}