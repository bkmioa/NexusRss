package io.github.bkmioa.nexusrss.util

import java.util.LinkedHashMap
import java.util.Locale

private val existingBbCodeRegex = Regex("(?is)\\[([a-z][a-z0-9]*)(?:=[^\\]]+)?].*?\\[/\\1]")
private val fencedCodeRegex = Regex("(?s)(```|~~~)([\\w#+-]*)\\s*\\n(.*?)\\n?\\1")
private val inlineCodeRegex = Regex("`([^`\\n]+?)`")
private val horizontalRuleRegex = Regex("(?m)^(?:([-*_])(\\s*\\1){2,})\\s*$")
private val headingRegex = Regex("(?m)^(#{1,6})\\s+(.+)$")
private val imageRegex = Regex("!\\[([^\\]]*)]\\(([^)\\s]+)(?:\\s+\"[^\"]*\")?\\)")
private val linkRegex = Regex("\\[([^\\]]+)]\\(([^)]+)\\)")
private val autoLinkRegex = Regex("<((?:https?|ftp)://[^>]+)>")
private val boldItalicAsteriskRegex = Regex("(?<!\\\\)\\*\\*\\*(.+?)(?<!\\\\)\\*\\*\\*")
private val boldItalicUnderscoreRegex = Regex("(?<!\\\\)___(.+?)(?<!\\\\)___")
private val boldAsteriskRegex = Regex("(?<!\\\\)\\*\\*(.+?)(?<!\\\\)\\*\\*")
private val boldUnderscoreRegex = Regex("(?<!\\\\)__(.+?)(?<!\\\\)__")
private val italicAsteriskRegex = Regex("(?<!\\\\)\\*(.+?)(?<!\\\\)\\*")
private val italicUnderscoreRegex = Regex("(?<!\\\\)_(.+?)(?<!\\\\)_")
private val strikeRegex = Regex("(?<!\\\\)~~(.+?)(?<!\\\\)~~")
private val unorderedListRegex = Regex("""^(\s*)([-+*])\s+(.*)$""")
private val orderedListRegex = Regex("""^(\s*)(\d+)[.)]\s+(.*)$""")
private val escapedCharRegex = Regex("""\\([\\`*_{}\[\]()#+\-.!>])""")

fun convertMarkdown2BBCode(content: String?): String {
    val raw = content?.takeIf { it.isNotBlank() } ?: return ""
    val placeholders = PlaceholderStore()
    var text = raw.replace("\r\n", "\n")
    text = maskExistingBbCodeLines(text, placeholders)

    text = convertBlockquotes(text)
    text = extractFencedCodeBlocks(text, placeholders)
    text = extractInlineCode(text, placeholders)
    text = convertHorizontalRules(text)
    text = convertHeadings(text)
    text = convertImages(text)
    text = convertLinks(text)
    text = convertAutoLinks(text)
    text = convertLists(text)
    text = convertEmphasis(text)
    text = convertStrikethrough(text)
    text = cleanupEscapes(text)
    text = text.replace(Regex("\n{3,}"), "\n\n")

    return placeholders.restore(text).trim()
}

private fun maskExistingBbCodeLines(text: String, placeholders: PlaceholderStore): String {
    var hasBbCode = false
    val lines = text.split("\n")
    val builder = StringBuilder(text.length)
    lines.forEachIndexed { index, line ->
        val processed = if (line.isExistingBbCodeLine()) {
            hasBbCode = true
            placeholders.store(line)
        } else {
            line
        }
        builder.append(processed)
        if (index != lines.lastIndex) {
            builder.append('\n')
        }
    }
    return if (hasBbCode) builder.toString() else text
}

private fun String.isExistingBbCodeLine(): Boolean {
    val trimmed = trim()
    if (trimmed.isEmpty()) return false
    return existingBbCodeRegex.matches(trimmed)
}

private fun extractFencedCodeBlocks(text: String, placeholders: PlaceholderStore): String {
    if (!text.contains("```") && !text.contains("~~~")) return text
    return fencedCodeRegex.replace(text) { match ->
        val language = match.groupValues[2].trim().lowercase(Locale.ROOT)
        val tag = if (language == "php") "php" else "code"
        val code = match.groupValues[3].trimEnd('\n', '\r')
        val block = "[${tag}]$code[/${tag}]"
        placeholders.store(block)
    }
}

private fun extractInlineCode(text: String, placeholders: PlaceholderStore): String {
    if (!text.contains('`')) return text
    return inlineCodeRegex.replace(text) { match ->
        val snippet = match.groupValues[1]
        placeholders.store("[font=mono]$snippet[/font]")
    }
}

private fun convertHorizontalRules(text: String): String =
    horizontalRuleRegex.replace(text) { "[hr]" }

private fun convertHeadings(text: String): String =
    headingRegex.replace(text) { match ->
        val level = match.groupValues[1].length
        val headingText = match.groupValues[2].trimHeadingClosingHashes()
        val size = when (level) {
            1 -> 9
            2 -> 8
            3 -> 7
            4 -> 6
            5 -> 5
            else -> 5
        }
        "[size=$size][b]$headingText[/b][/size]"
    }

private fun String.trimHeadingClosingHashes(): String {
    var end = length
    while (end > 0 && this[end - 1] == '#') end--
    return substring(0, end).trim()
}

private fun convertImages(text: String): String =
    imageRegex.replace(text) { match ->
        val url = match.groupValues[2].trim()
        "[img]$url[/img]"
    }

private fun convertLinks(text: String): String =
    linkRegex.replace(text) { match ->
        val label = match.groupValues[1].trim()
        val target = match.groupValues[2].trim()
        "[url=$target]$label[/url]"
    }

private fun convertAutoLinks(text: String): String =
    autoLinkRegex.replace(text) { match ->
        val url = match.groupValues[1].trim()
        "[url=$url]$url[/url]"
    }

private enum class ListType { ORDERED, UNORDERED }

private fun convertLists(text: String): String {
    val lines = text.split("\n")
    if (lines.none { unorderedListRegex.matches(it.trimStart()) || orderedListRegex.matches(it.trimStart()) }) {
        return text
    }
    val builder = StringBuilder()
    var currentType: ListType? = null

    fun open(type: ListType) {
        if (currentType == type) return
        closeCurrent(builder, currentType)
        builder.append(if (type == ListType.ORDERED) "[ol]" else "[ul]").append('\n')
        currentType = type
    }

    lines.forEach { line ->
        val unordered = unorderedListRegex.matchEntire(line)
        val ordered = orderedListRegex.matchEntire(line)
        when {
            unordered != null -> {
                open(ListType.UNORDERED)
                val content = unordered.groupValues[3].trim()
                if (content.isNotEmpty()) {
                    builder.append("[li]").append(content).append("[/li]").append('\n')
                }
            }
            ordered != null -> {
                open(ListType.ORDERED)
                val content = ordered.groupValues[3].trim()
                if (content.isNotEmpty()) {
                    builder.append("[li]").append(content).append("[/li]").append('\n')
                }
            }
            line.isBlank() -> {
                closeCurrent(builder, currentType)
                builder.append('\n')
                currentType = null
            }
            else -> {
                closeCurrent(builder, currentType)
                currentType = null
                builder.append(line).append('\n')
            }
        }
    }

    closeCurrent(builder, currentType)
    return builder.toString().trimEnd('\n')
}

private fun closeCurrent(builder: StringBuilder, currentType: ListType?) {
    when (currentType) {
        ListType.ORDERED -> builder.append("[/ol]").append('\n')
        ListType.UNORDERED -> builder.append("[/ul]").append('\n')
        null -> Unit
    }
}

private fun convertEmphasis(text: String): String {
    var result = text
    result = boldItalicAsteriskRegex.replace(result) { "[b][i]${it.groupValues[1]}[/i][/b]" }
    result = boldItalicUnderscoreRegex.replace(result) { "[b][i]${it.groupValues[1]}[/i][/b]" }
    result = boldAsteriskRegex.replace(result) { "[b]${it.groupValues[1]}[/b]" }
    result = boldUnderscoreRegex.replace(result) { "[b]${it.groupValues[1]}[/b]" }
    result = italicAsteriskRegex.replace(result) { "[i]${it.groupValues[1]}[/i]" }
    result = italicUnderscoreRegex.replace(result) { "[i]${it.groupValues[1]}[/i]" }
    return result
}

private fun convertStrikethrough(text: String): String =
    strikeRegex.replace(text) { "[s]${it.groupValues[1]}[/s]" }

private fun convertBlockquotes(text: String): String {
    if (!text.contains(">")) return text
    val lines = text.split("\n")
    val builder = StringBuilder()
    val quoteBuffer = StringBuilder()
    var inQuote = false

    fun flushQuote() {
        if (!inQuote) return
        val content = quoteBuffer.toString().trimEnd('\n')
        if (content.isNotBlank()) {
            builder.append("[quote]\n").append(content).append("\n[/quote]\n")
        } else {
            builder.append("[quote][/quote]\n")
        }
        quoteBuffer.clear()
        inQuote = false
    }

    lines.forEach { rawLine ->
        val trimmed = rawLine.trimStart()
        if (trimmed.startsWith(">")) {
            if (!inQuote) {
                inQuote = true
            }
            val level = trimmed.takeWhile { it == '>' }.length
            val content = trimmed.drop(level).trimStart()
            if (level > 1) {
                quoteBuffer.append(">".repeat(level - 1)).append(' ')
            }
            quoteBuffer.append(content).append('\n')
        } else if (rawLine.isBlank() && inQuote) {
            quoteBuffer.append('\n')
        } else {
            flushQuote()
            builder.append(rawLine).append('\n')
        }
    }

    flushQuote()
    return builder.toString().trimEnd('\n')
}

private fun cleanupEscapes(text: String): String =
    escapedCharRegex.replace(text) { match -> match.groupValues[1] }

private class PlaceholderStore {
    private var counter = 0
    private val values = LinkedHashMap<String, String>()

    fun store(value: String): String {
        val token = "%MD_PLACEHOLDER-${counter++}%"
        values[token] = value
        return token
    }

    fun restore(text: String): String {
        var result = text
        values.forEach { (token, value) ->
            result = result.replace(token, value)
        }
        return result
    }
}
