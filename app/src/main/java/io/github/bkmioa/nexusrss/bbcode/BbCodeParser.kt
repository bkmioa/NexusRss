package io.github.bkmioa.nexusrss.bbcode

import java.util.Locale

internal class BbCodeParser {

    fun parse(input: String): List<BbNode> {
        if (input.isBlank()) return emptyList()
        val normalized = input.replace("\r\n", "\n")
        return ParserState(normalized).parseNodes()
    }

    private class ParserState(private val source: String) {
        private var index = 0
        private val length = source.length

        fun parseNodes(stopTag: String? = null, listDepth: Int = 0): List<BbNode> {
            val nodes = mutableListOf<BbNode>()
            val textBuffer = StringBuilder()

            while (index < length) {
                val current = source[index]
                if (current == '[') {
                    val tagStart = index
                    val tag = readTag() ?: run {
                        textBuffer.append(source[index])
                        index++
                        continue
                    }

                    if (tag.isClosing) {
                        if (stopTag != null && tag.name == stopTag) {
                            flushText(textBuffer, nodes)
                            return nodes
                        } else {
                            textBuffer.append("[")
                            textBuffer.append(tag.raw)
                            textBuffer.append("]")
                        }
                        continue
                    }

                    flushText(textBuffer, nodes)
                    when (tag.name) {
                        "b" -> nodes += BbNode.Styled(InlineStyle.Bold, parseNodes("b", listDepth))
                        "i" -> nodes += BbNode.Styled(InlineStyle.Italic, parseNodes("i", listDepth))
                        "u" -> nodes += BbNode.Styled(InlineStyle.Underline, parseNodes("u", listDepth))
                        "s" -> nodes += BbNode.Styled(InlineStyle.Strike, parseNodes("s", listDepth))
                        "color" -> {
                            val value = tag.value.orEmpty()
                            nodes += BbNode.Styled(InlineStyle.Color(value), parseNodes("color", listDepth))
                        }

                        "size" -> {
                            val size = tag.value?.toIntOrNull()?.times(3)?.coerceIn(12, 36) ?: 14
                            nodes += BbNode.Styled(InlineStyle.Size(size), parseNodes("size", listDepth))
                        }

                        "small" -> {
                            val color = tag.value
                            val children = parseNodes("small", listDepth)
                            val styled = if (!color.isNullOrBlank()) {
                                listOf(BbNode.Styled(InlineStyle.Color(color), children))
                            } else {
                                children
                            }
                            nodes += BbNode.Styled(InlineStyle.Size(10), styled)
                        }

                        "large" -> {
                            val color = tag.value
                            val children = parseNodes("large", listDepth)
                            val styled = if (!color.isNullOrBlank()) {
                                listOf(BbNode.Styled(InlineStyle.Color(color), children))
                            } else {
                                children
                            }
                            nodes += BbNode.Styled(InlineStyle.Size(36), styled)
                        }

                        "font", "face" -> {
                            val font = tag.value
                            if (font.isNullOrBlank()) {
                                nodes += parseNodes(tag.name, listDepth)
                            } else {
                                nodes += BbNode.Styled(InlineStyle.Font(font), parseNodes(tag.name, listDepth))
                            }
                        }

                        "sub" -> nodes += BbNode.Styled(InlineStyle.Subscript, parseNodes("sub", listDepth))
                        "sup" -> nodes += BbNode.Styled(InlineStyle.Superscript, parseNodes("sup", listDepth))
                        "center" -> nodes += BbNode.Alignment(AlignmentType.CENTER, parseNodes("center", listDepth))
                        "left" -> nodes += BbNode.Alignment(AlignmentType.LEFT, parseNodes("left", listDepth))
                        "right" -> nodes += BbNode.Alignment(AlignmentType.RIGHT, parseNodes("right", listDepth))
                        "justify" -> nodes += BbNode.Alignment(AlignmentType.JUSTIFY, parseNodes("justify", listDepth))
                        "url" -> nodes += handleUrl(tag, listDepth)
                        "email" -> nodes += handleEmail(tag, listDepth)
                        "img" -> parseImage()?.let(nodes::add)
                        "quote" -> nodes += BbNode.Quote(tag.value, parseNodes("quote", listDepth))
                        "code" -> nodes += BbNode.Code(readRawContent("code"), null)
                        "php" -> nodes += BbNode.Code(readRawContent("php"), "php")
                        "noparse" -> nodes += BbNode.Text(readRawContent("noparse"))
                        "bbcode" -> nodes += parseNodes("bbcode", listDepth)
                        "hr" -> nodes += BbNode.HorizontalRule
                        "br" -> nodes += BbNode.LineBreak
                        "list" -> nodes += parseList(ordered = false, listDepth = listDepth + 1, stopTag = "list")
                        "ul" -> nodes += parseList(ordered = false, listDepth = listDepth + 1, stopTag = "ul")
                        "ol" -> nodes += parseList(ordered = true, listDepth = listDepth + 1, stopTag = "ol")
                        "li" -> nodes += BbNode.ListItem(parseNodes("li", listDepth + 1))
                        "table" -> parseTable()?.let(nodes::add)
                        "tbody", "thead", "tfoot", "tr", "td", "th" -> {
                            // These are handled as part of [table]; if they appear alone, treat them as text.
                            textBuffer.append("[")
                            textBuffer.append(tag.raw)
                            textBuffer.append("]")
                        }

                        "*" -> {
                            if (listDepth > 0) {
                                nodes += BbNode.ListItemBreak
                            } else {
                                textBuffer.append(source.substring(tagStart, index))
                            }
                        }

                        else -> {
                            // Unknown tag -> treat content literally
                            textBuffer.append(source.substring(tagStart, index))
                        }
                    }
                } else {
                    textBuffer.append(current)
                    index++
                }
            }

            flushText(textBuffer, nodes)
            return nodes
        }

        private fun parseList(ordered: Boolean, listDepth: Int, stopTag: String): BbNode {
            val children = parseNodes(stopTag, listDepth)
            val items = splitListItems(children)
            return BbNode.ListBlock(ordered, items)
        }

        private fun splitListItems(children: List<BbNode>): List<List<BbNode>> {
            val items = mutableListOf<List<BbNode>>()
            var current = mutableListOf<BbNode>()
            var sawExplicitBreak = false

            fun flush() {
                if (current.isNotEmpty()) {
                    items += current
                    current = mutableListOf()
                }
            }

            children.forEach { child ->
                when (child) {
                    is BbNode.ListItemBreak -> {
                        sawExplicitBreak = true
                        flush()
                    }

                    is BbNode.ListItem -> {
                        flush()
                        if (child.children.isNotEmpty()) {
                            items += child.children
                        }
                        sawExplicitBreak = true
                    }

                    else -> current += child
                }
            }

            flush()
            if (!sawExplicitBreak && items.isEmpty() && children.isNotEmpty()) {
                items += children
            }

            return items.filter { it.isNotEmpty() }
        }

        private fun handleUrl(tag: ParsedTag, listDepth: Int): List<BbNode> {
            val children = parseNodes("url", listDepth)
            val imageNodes = extractImages(children)
            if (imageNodes.isNotEmpty()) {
                return imageNodes
            }

            val plain = plainText(children)
            val fallback = plain.ifBlank { null }
            val target = tag.value ?: fallback ?: return listOf(BbNode.Text(plain))
            val normalized = target.trim()
            val display = if (children.isEmpty()) listOf(BbNode.Text(normalized)) else children
            return listOf(BbNode.Link(normalized, display))
        }

        private fun extractImages(nodes: List<BbNode>): List<BbNode.Image> {
            if (nodes.isEmpty()) return emptyList()
            val images = mutableListOf<BbNode.Image>()
            nodes.forEach { collectImages(it, images) }
            return images
        }

        private fun collectImages(node: BbNode, accumulator: MutableList<BbNode.Image>) {
            when (node) {
                is BbNode.Image -> accumulator += node
                is BbNode.Styled -> node.children.forEach { collectImages(it, accumulator) }
                is BbNode.Alignment -> node.children.forEach { collectImages(it, accumulator) }
                is BbNode.Link -> node.children.forEach { collectImages(it, accumulator) }
                is BbNode.Quote -> node.children.forEach { collectImages(it, accumulator) }
                is BbNode.ListItem -> node.children.forEach { collectImages(it, accumulator) }
                is BbNode.ListBlock -> node.items.forEach { item -> item.forEach { collectImages(it, accumulator) } }
                is BbNode.Table -> node.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        cell.children.forEach { collectImages(it, accumulator) }
                    }
                }

                is BbNode.Code,
                is BbNode.Text,
                BbNode.HorizontalRule,
                BbNode.LineBreak,
                BbNode.ListItemBreak -> {
                    // No images to collect
                }
            }
        }

        private fun handleEmail(tag: ParsedTag, listDepth: Int): BbNode {
            val children = parseNodes("email", listDepth)
            val fallback = plainText(children).ifBlank { null }
            val email = tag.value ?: fallback ?: return BbNode.Text(plainText(children))
            val target = "mailto:${email.trim()}"
            val display = if (children.isEmpty()) listOf(BbNode.Text(email.trim())) else children
            return BbNode.Link(target, display)
        }

        private fun parseImage(): BbNode? {
            val raw = readRawContent("img").trim()
            if (raw.isEmpty()) return null
            return BbNode.Image(raw)
        }

        private fun parseTable(): BbNode? {
            val raw = readRawContent("table")
            if (raw.isEmpty()) return null
            val cleaned = raw.replace(Regex("(?is)\\[(?:/)?(?:thead|tbody|tfoot)]"), "")
            val rowPattern = Regex("(?is)\\[tr(?:=[^\\]]+)?](.*?)\\[/tr]")
            val cellPattern = Regex("(?is)\\[(td|th)(?:=[^\\]]+)?](.*?)\\[/\\1]")
            val rows = mutableListOf<TableRow>()

            rowPattern.findAll(cleaned).forEach { rowMatch ->
                val cells = mutableListOf<TableCell>()
                val body = rowMatch.groupValues[1]
                cellPattern.findAll(body).forEach { cellMatch ->
                    val isHeader = cellMatch.groupValues[1].equals("th", ignoreCase = true)
                    val content = cellMatch.groupValues[2]
                    val parsed = ParserState(content).parseNodes()
                    cells += TableCell(isHeader, parsed)
                }
                if (cells.isNotEmpty()) {
                    rows += TableRow(cells)
                }
            }

            if (rows.isEmpty()) {
                val fallback = ParserState(raw).parseNodes()
                return BbNode.Table(listOf(TableRow(listOf(TableCell(false, fallback)))))
            }

            return BbNode.Table(rows)
        }

        private fun readRawContent(tagName: String): String {
            val target = tagName.lowercase(Locale.getDefault())
            val start = index
            var depth = 1

            while (index < length) {
                val nextOpen = source.indexOf('[', index)
                if (nextOpen == -1) {
                    val raw = source.substring(start)
                    index = length
                    return raw
                }
                val close = source.indexOf(']', nextOpen)
                if (close == -1) {
                    val raw = source.substring(start)
                    index = length
                    return raw
                }
                val content = source.substring(nextOpen + 1, close).trim()
                index = close + 1
                if (content.isEmpty()) continue
                val isClosing = content.startsWith("/")
                val name = extractName(content)
                if (name == target) {
                    if (isClosing) {
                        depth--
                        if (depth == 0) {
                            return source.substring(start, nextOpen)
                        }
                    } else {
                        depth++
                    }
                }
            }

            return source.substring(start)
        }

        private fun flushText(buffer: StringBuilder, nodes: MutableList<BbNode>) {
            if (buffer.isNotEmpty()) {
                val text = buffer.toString()
                buffer.clear()
                val previous = nodes.lastOrNull()
                if (text.isBlank() && previous != null && isBlockNode(previous)) {
                    return
                }
                nodes += BbNode.Text(text)
            }
        }

        private fun isBlockNode(node: BbNode): Boolean {
            return when (node) {
                is BbNode.Alignment -> true
                is BbNode.Code -> true
                is BbNode.Image -> true
                is BbNode.ListBlock -> true
                is BbNode.Quote -> true
                is BbNode.Table -> true
                BbNode.HorizontalRule -> true
                BbNode.LineBreak -> true
                else -> false
            }
        }

        private fun readTag(): ParsedTag? {
            if (source[index] != '[') return null
            val end = source.indexOf(']', index)
            if (end == -1) return null
            val rawContent = source.substring(index + 1, end)
            index = end + 1
            val trimmed = rawContent.trim()
            if (trimmed.isEmpty()) return null
            val isClosing = trimmed.startsWith("/")
            val body = if (isClosing) trimmed.substring(1) else trimmed
            val name = extractName(body)
            if (name.isEmpty()) return null
            val params = body.substring(name.length).trim()
            val value = params.removePrefix("=").trim().trim('"')

            return ParsedTag(
                name = name,
                raw = rawContent,
                params = params.ifBlank { null },
                value = value.ifBlank { null },
                isClosing = isClosing
            )
        }

        private fun plainText(nodes: List<BbNode>): String {
            val builder = StringBuilder()
            nodes.forEach { appendPlainText(it, builder) }
            return builder.toString()
        }

        private fun appendPlainText(node: BbNode, builder: StringBuilder) {
            when (node) {
                is BbNode.Text -> builder.append(node.value)
                is BbNode.Styled -> node.children.forEach { appendPlainText(it, builder) }
                is BbNode.Link -> node.children.forEach { appendPlainText(it, builder) }
                is BbNode.Quote -> node.children.forEach { appendPlainText(it, builder) }
                is BbNode.Code -> builder.append(node.code)
                is BbNode.Image -> builder.append(node.url)
                is BbNode.Alignment -> node.children.forEach { appendPlainText(it, builder) }
                is BbNode.ListBlock -> node.items.forEachIndexed { index, item ->
                    if (index > 0) builder.append('\n')
                    item.forEach { appendPlainText(it, builder) }
                }

                is BbNode.Table -> node.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        cell.children.forEach { appendPlainText(it, builder) }
                        builder.append('\t')
                    }
                    builder.append('\n')
                }

                BbNode.LineBreak -> builder.append('\n')
                BbNode.HorizontalRule -> builder.append('\n')
                BbNode.ListItemBreak -> builder.append('\n')
                is BbNode.ListItem -> node.children.forEach { appendPlainText(it, builder) }
            }
        }

        private fun extractName(content: String): String {
            val trimmed = content.trim()
            if (trimmed.isEmpty()) return ""
            var start = 0
            if (trimmed.startsWith("/")) start = 1
            val builder = StringBuilder()
            for (i in start until trimmed.length) {
                val ch = trimmed[i]
                if (ch.isWhitespace() || ch == '=') {
                    break
                }
                builder.append(ch.lowercaseChar())
            }
            return builder.toString()
        }
    }

    private data class ParsedTag(
        val name: String,
        val raw: String,
        val params: String?,
        val value: String?,
        val isClosing: Boolean
    )
}
