package io.github.bkmioa.nexusrss.detail

import io.github.bkmioa.nexusrss.model.FileItem


data class FileNode(
    val name: String,
    val depth: Int,
    val children: MutableList<FileNode> = mutableListOf(),
    var sizeText: String? = null,
) {
    val isDirectory: Boolean
        get() = children.isNotEmpty()
}

fun List<FileItem>.toHierarchy(): List<FileNode> {
    val tempRoot = FileNode(name = "", depth = -1)

    for (file in this) {
        val segments = file.name.split('/')

        if (segments.isEmpty()) continue

        var current = tempRoot
        segments.forEachIndexed { index, segment ->
            val child = current.children.find { it.name == segment }
                ?: FileNode(name = segment, depth = current.depth + 1).also { current.children.add(it) }

            if (index == segments.lastIndex) {
                child.sizeText = file.sizeText
            } else {
                child.sizeText = null
            }

            current = child
        }
    }
    return tempRoot.children
}