package io.github.bkmioa.nexusrss.model

class Result<T> {
    var code: Int = 0

    var message: String = ""

    var data: T? = null
}