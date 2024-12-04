package io.github.bkmioa.nexusrss.model

class ItemList<T> {
    var pageNumber: Int = 0

    var pageSize: Int = 0

    var total: Int = 0

    var totalPages: Int = 0

    var data: List<T> = emptyList()
}