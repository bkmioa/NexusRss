package io.github.bkmioa.nexusrss.bbcode

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnit.Companion.Unspecified
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun BbCodeContent(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
    onLinkClick: ((String) -> Boolean)? = null
) {
    if (text.isBlank()) return
    val nodes = remember(text) { BbCodeParser().parse(text) }
    if (nodes.isEmpty()) return

    SelectionContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            val uriHandler = LocalUriHandler.current
            BbNodes(nodes, textStyle, uriHandler, onLinkClick)
        }
    }
}

@Composable
private fun BbNodes(
    nodes: List<BbNode>,
    textStyle: TextStyle,
    uriHandler: UriHandler,
    onLinkClick: ((String) -> Boolean)?
) {
    if (nodes.isEmpty()) return
    val inlineBuffer = mutableListOf<BbNode>()
    var isFirstBlock = true

    nodes.forEach { node ->
        if (node.isInline()) {
            inlineBuffer += node
        } else {
            if (inlineBuffer.isNotEmpty()) {
                if (!isFirstBlock) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                InlineTextBlock(inlineBuffer.toList(), textStyle, uriHandler, onLinkClick)
                inlineBuffer.clear()
                isFirstBlock = false
            }
            if (!isFirstBlock) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            BlockNode(node, textStyle, uriHandler, onLinkClick)
            isFirstBlock = false
        }
    }

    if (inlineBuffer.isNotEmpty()) {
        if (!isFirstBlock) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        InlineTextBlock(inlineBuffer.toList(), textStyle, uriHandler, onLinkClick)
    }
}

@Composable
private fun InlineTextBlock(
    nodes: List<BbNode>,
    textStyle: TextStyle,
    uriHandler: UriHandler,
    onLinkClick: ((String) -> Boolean)?
) {
    if (nodes.isEmpty()) return
    val linkColor = MaterialTheme.colorScheme.primary
    val linkInteraction = remember(onLinkClick, uriHandler) {
        LinkInteractionListener { annotation ->
            val url = (annotation as? LinkAnnotation.Url)?.url ?: return@LinkInteractionListener
            val handled = onLinkClick?.invoke(url) == true
            if (!handled) {
                runCatching { uriHandler.openUri(url) }
            }
        }
    }
    val annotated = remember(nodes, textStyle, linkColor, linkInteraction) {
        buildAnnotatedString {
            appendInlineNodes(nodes, textStyle, linkColor, InlineStyleState(), linkInteraction)
        }
    }
    if (annotated.isEmpty()) return
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = annotated,
        style = textStyle,
    )
}

@Composable
private fun BlockNode(
    node: BbNode,
    textStyle: TextStyle,
    uriHandler: UriHandler,
    onLinkClick: ((String) -> Boolean)?
) {
    when (node) {
        is BbNode.Quote -> QuoteBlock(node, textStyle, uriHandler, onLinkClick)
        is BbNode.Code -> CodeBlock(node, textStyle)
        is BbNode.Image -> ImageBlock(node.url)
        is BbNode.Alignment -> {
            val alignedStyle = textStyle.copy(
                textAlign = when (node.alignment) {
                    AlignmentType.LEFT -> TextAlign.Start
                    AlignmentType.CENTER -> TextAlign.Center
                    AlignmentType.RIGHT -> TextAlign.End
                    AlignmentType.JUSTIFY -> TextAlign.Justify
                }
            )
            BbNodes(node.children, alignedStyle, uriHandler, onLinkClick)
        }

        is BbNode.ListBlock -> ListBlock(node, textStyle, uriHandler, onLinkClick)
        is BbNode.Table -> TableBlock(node, textStyle, uriHandler, onLinkClick)
        BbNode.HorizontalRule -> HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        BbNode.LineBreak -> Spacer(modifier = Modifier.height(4.dp))
        is BbNode.Text -> InlineTextBlock(listOf(node), textStyle, uriHandler, onLinkClick)
        is BbNode.Styled, is BbNode.Link -> InlineTextBlock(listOf(node), textStyle, uriHandler, onLinkClick)
        else -> {}
    }
}

@Composable
private fun QuoteBlock(
    node: BbNode.Quote,
    textStyle: TextStyle,
    uriHandler: UriHandler,
    onLinkClick: ((String) -> Boolean)?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            node.source?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = textStyle.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                )
            }
            BbNodes(node.children, textStyle, uriHandler, onLinkClick)
        }
    }
}

@Composable
private fun CodeBlock(node: BbNode.Code, textStyle: TextStyle) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 2.dp,
    ) {
        Text(
            text = node.code.trimEnd(),
            style = textStyle.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
    }
}

@Composable
private fun ImageBlock(url: String) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        modifier = Modifier
            .fillMaxWidth(),
        model = ImageRequest.Builder(context)
            .data(url.trim())
            .crossfade(true)
            .build(),
        contentScale = ContentScale.FillWidth,
        contentDescription = null,
        loading = {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            }
        },
    )
}

@Composable
private fun ListBlock(
    node: BbNode.ListBlock,
    textStyle: TextStyle,
    uriHandler: UriHandler,
    onLinkClick: ((String) -> Boolean)?
) {
    if (node.items.isEmpty()) return
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        node.items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val marker = if (node.ordered) "${index + 1}." else "\u2022"
                Text(
                    text = marker,
                    style = textStyle.copy(fontWeight = FontWeight.SemiBold)
                )
                Column(modifier = Modifier.weight(1f, fill = true)) {
                    BbNodes(item, textStyle, uriHandler, onLinkClick)
                }
            }
        }
    }
}

@Composable
private fun TableBlock(
    node: BbNode.Table,
    textStyle: TextStyle,
    uriHandler: UriHandler,
    onLinkClick: ((String) -> Boolean)?
) {
    if (node.rows.isEmpty()) return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            node.rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.cells.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            val cellStyle = if (cell.header) {
                                textStyle.copy(fontWeight = FontWeight.Bold)
                            } else {
                                textStyle
                            }
                            BbNodes(cell.children, cellStyle, uriHandler, onLinkClick)
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
            }
        }
    }
}

private fun AnnotatedString.Builder.appendInlineNodes(
    nodes: List<BbNode>,
    textStyle: TextStyle,
    linkColor: Color,
    state: InlineStyleState,
    linkInteraction: LinkInteractionListener
) {
    nodes.forEach { node ->
        when (node) {
            is BbNode.Text -> append(node.value)
            BbNode.LineBreak -> append("\n")
            is BbNode.Styled -> {
                val next = state.apply(node.style)
                withStyle(next.toSpanStyle(textStyle, linkColor)) {
                    appendInlineNodes(node.children, textStyle, linkColor, next, linkInteraction)
                }
            }

            is BbNode.Link -> {
                val linkState = state.forLink()
                pushLink(
                    LinkAnnotation.Url(
                        url = node.target,
                        linkInteractionListener = linkInteraction
                    )
                )
                withStyle(linkState.toSpanStyle(textStyle, linkColor)) {
                    appendInlineNodes(node.children, textStyle, linkColor, linkState, linkInteraction)
                }
                pop()
            }

            else -> append(plainText(node))
        }
    }
}

private fun BbNode.isInline(): Boolean =
    this is BbNode.Text ||
            this is BbNode.Styled ||
            this is BbNode.Link ||
            this === BbNode.LineBreak

private data class InlineStyleState(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strike: Boolean = false,
    val colorOverride: Color? = null,
    val size: TextUnit? = null,
    val fontFamily: FontFamily? = null,
    val baselineShift: BaselineShift? = null,
    val isLink: Boolean = false
) {
    fun apply(style: InlineStyle): InlineStyleState {
        return when (style) {
            InlineStyle.Bold -> copy(bold = true)
            InlineStyle.Italic -> copy(italic = true)
            InlineStyle.Underline -> copy(underline = true)
            InlineStyle.Strike -> copy(strike = true)
            is InlineStyle.Color -> copy(colorOverride = parseColorSafe(style.value) ?: colorOverride)
            is InlineStyle.Size -> copy(size = style.points.sp)
            is InlineStyle.Font -> copy(fontFamily = mapFontFamily(style.family) ?: fontFamily)
            InlineStyle.Subscript -> copy(baselineShift = BaselineShift.Subscript)
            InlineStyle.Superscript -> copy(baselineShift = BaselineShift.Superscript)
        }
    }

    fun forLink(): InlineStyleState = copy(
        underline = true,
        colorOverride = null,
        isLink = true
    )

    fun toSpanStyle(baseStyle: TextStyle, linkColor: Color): SpanStyle {

        val decorations = mutableListOf<TextDecoration>()
        if (underline || isLink) decorations += TextDecoration.Underline
        if (strike) decorations += TextDecoration.LineThrough

        val resolvedColor = colorOverride
            ?: if (isLink) linkColor else baseStyle.color

        val resolvedSize = size ?: baseStyle.fontSize.takeUnless { it == Unspecified } ?: Unspecified

        return baseStyle.toSpanStyle().merge(
            SpanStyle(
                fontWeight = if (bold) FontWeight.Bold else null,
                fontStyle = if (italic) FontStyle.Italic else null,
                textDecoration = if (decorations.isNotEmpty()) TextDecoration.combine(decorations) else null,
                color = resolvedColor,
                fontSize = resolvedSize,
                fontFamily = fontFamily,
                baselineShift = baselineShift
            )
        )
    }
}

private fun parseColorSafe(value: String): Color? {
    return runCatching { Color(android.graphics.Color.parseColor(value.trim())) }.getOrNull()
}

private fun mapFontFamily(name: String): FontFamily? {
    val lower = name.trim().lowercase()
    return when {
        "mono" in lower -> FontFamily.Monospace
        "serif" in lower -> FontFamily.Serif
        "sans" in lower -> FontFamily.SansSerif
        "cursive" in lower || "script" in lower -> FontFamily.Cursive
        else -> null
    }
}

private fun plainText(node: BbNode): String = buildString {
    fun appendNode(current: BbNode) {
        when (current) {
            is BbNode.Text -> append(current.value)
            is BbNode.Styled -> current.children.forEach { appendNode(it) }
            is BbNode.Link -> current.children.forEach { appendNode(it) }
            is BbNode.Quote -> current.children.forEach { appendNode(it) }
            is BbNode.Code -> append(current.code)
            is BbNode.Image -> append(current.url)
            is BbNode.Alignment -> current.children.forEach { appendNode(it) }
            is BbNode.ListBlock -> current.items.forEach { item ->
                item.forEach { appendNode(it) }
                append('\n')
            }

            is BbNode.Table -> current.rows.forEach { row ->
                row.cells.forEach { cell ->
                    cell.children.forEach { appendNode(it) }
                    append('\t')
                }
                append('\n')
            }

            BbNode.LineBreak -> append('\n')
            BbNode.HorizontalRule -> append('\n')
            is BbNode.ListItem -> current.children.forEach { appendNode(it) }
            BbNode.ListItemBreak -> append('\n')
        }
    }
    appendNode(node)
}
