package com.xnotes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xnotes.R
import com.xnotes.core.model.Page
import com.xnotes.ui.icons.XnotesIcons
import com.xnotes.ui.theme.LocalPalette
import com.xnotes.ui.theme.toComposeColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Minimal reader controls: home/page-preview on the left, page navigation on the right. */
@Composable
internal fun ReaderChrome(editor: Editor, onHome: () -> Unit) {
    val palette = LocalPalette.current
    Box(Modifier.fillMaxSize()) {
        Row(
            Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = palette.panel.toComposeColor(),
                shadowElevation = 6.dp,
                tonalElevation = 0.dp,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onHome) {
                        Icon(XnotesIcons.home, stringResource(R.string.back_to_home), tint = palette.text.toComposeColor())
                    }
                    IconButton(onClick = { editor.sidebarVisible = !editor.sidebarVisible }) {
                        Icon(
                            XnotesIcons.thumbnails,
                            stringResource(R.string.pages),
                            tint = if (editor.sidebarVisible) palette.accent.toComposeColor() else palette.text.toComposeColor(),
                        )
                    }
                }
            }
        }
        Surface(
            Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 14.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = palette.panel.toComposeColor(),
            shadowElevation = 6.dp,
            tonalElevation = 0.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = editor::prevPage, enabled = editor.pageIndex > 0) {
                    Icon(XnotesIcons.prev, stringResource(R.string.previous_page), tint = palette.text.toComposeColor())
                }
                Text(
                    "${editor.pageIndex + 1} / ${editor.pageCount}",
                    color = palette.text.toComposeColor(),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                IconButton(onClick = editor::nextPage, enabled = editor.pageIndex < editor.pageCount - 1) {
                    Icon(XnotesIcons.next, stringResource(R.string.next_page), tint = palette.text.toComposeColor())
                }
            }
        }
        if (editor.sidebarVisible) {
            ReaderPagePreview(editor, Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 76.dp))
        }
    }
}

/** Read-only page thumbnails. No page insertion, editing, deletion, or rearrangement actions. */
@Composable
private fun ReaderPagePreview(editor: Editor, modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    val pages = remember(editor.contentVersion) { editor.pagesSnapshot() }
    Surface(
        modifier.width(132.dp).fillMaxHeight(0.68f),
        shape = MaterialTheme.shapes.large,
        color = palette.panel.toComposeColor(),
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(pages, key = { _, page -> page.uid }) { index, page ->
                ReaderPageThumbnail(
                    editor = editor,
                    page = page,
                    index = index,
                    selected = index == editor.pageIndex,
                    onClick = { editor.goToPage(index) },
                )
            }
        }
    }
}

@Composable
private fun ReaderPageThumbnail(
    editor: Editor,
    page: Page,
    index: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    val thumb = produceState<ImageBitmap?>(null, editor, page, editor.contentVersion) {
        value = withContext(Dispatchers.Default) { editor.pageThumbnail(page, 112) }
    }.value
    Column(
        Modifier
            .background(
                if (selected) palette.accent.copy(alpha = 0.18f).toComposeColor()
                else palette.surface.toComposeColor(),
                MaterialTheme.shapes.small,
            )
            .clickable(onClick = onClick)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (thumb != null) {
            Image(
                bitmap = thumb,
                contentDescription = stringResource(R.string.page_number, index + 1),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().height(104.dp),
            )
        } else {
            Box(Modifier.fillMaxSize().height(104.dp), contentAlignment = Alignment.Center) {
                Text((index + 1).toString(), color = palette.textDim.toComposeColor(), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(3.dp))
        Text((index + 1).toString(), color = palette.text.toComposeColor(), fontSize = 11.sp)
    }
}
