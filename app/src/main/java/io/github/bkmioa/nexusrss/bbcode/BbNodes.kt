package io.github.bkmioa.nexusrss.bbcode

import androidx.compose.ui.text.font.FontFamily

internal sealed interface BbNode {
    data class Text(val value: String) : BbNode
    data class Styled(val style: InlineStyle, val children: List<BbNode>) : BbNode
    data class Link(val target: String, val children: List<BbNode>) : BbNode
    data class Quote(val source: String?, val children: List<BbNode>) : BbNode
    data class Code(val code: String, val language: String? = null) : BbNode
    data class Image(val url: String) : BbNode
    data class Alignment(val alignment: AlignmentType, val children: List<BbNode>) : BbNode
    data class ListBlock(val ordered: Boolean, val items: List<List<BbNode>>) : BbNode
    data class Table(val rows: List<TableRow>) : BbNode

    data object LineBreak : BbNode
    data object HorizontalRule : BbNode

    /**
     * Internal helpers used while parsing lists.
     */
    data object ListItemBreak : BbNode
    data class ListItem(val children: List<BbNode>) : BbNode
}

internal enum class AlignmentType {
    LEFT, CENTER, RIGHT, JUSTIFY
}

internal sealed interface InlineStyle {
    data object Bold : InlineStyle
    data object Italic : InlineStyle
    data object Underline : InlineStyle
    data object Strike : InlineStyle
    data class Color(val value: String) : InlineStyle
    data class Size(val points: Int) : InlineStyle
    data class Font(val family: String) : InlineStyle
    data object Subscript : InlineStyle
    data object Superscript : InlineStyle
}

internal data class TableRow(val cells: List<TableCell>)
internal data class TableCell(val header: Boolean, val children: List<BbNode>)
