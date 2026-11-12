package com.xnotes.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xnotes.R
import com.xnotes.ui.icons.XnotesIcons
import com.xnotes.ui.theme.LocalPalette
import com.xnotes.ui.theme.toComposeColor

/** Reader controls: native app sidebar, native View menu, and page navigation. */
@Composable
internal fun ReaderChrome(editor: Editor, onHome: () -> Unit) {
    val palette = LocalPalette.current
    val glass = palette.panel.toComposeColor().copy(alpha = 0.62f)
    val edge = palette.border.toComposeColor().copy(alpha = 0.46f)
    Box(Modifier.fillMaxSize()) {
        Row(
            Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.border(1.dp, edge, MaterialTheme.shapes.extraLarge),
                shape = MaterialTheme.shapes.extraLarge,
                color = glass,
                shadowElevation = 8.dp,
                tonalElevation = 0.dp,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onHome) {
                        Icon(XnotesIcons.home, stringResource(R.string.back_to_home), tint = palette.text.toComposeColor())
                    }
                    IconButton(onClick = editor::toggleSidebar) {
                        Icon(
                            XnotesIcons.sidebar,
                            stringResource(R.string.pages),
                            tint = if (editor.sidebarVisible) palette.accent.toComposeColor() else palette.text.toComposeColor(),
                        )
                    }
                }
            }
        }
        Surface(
            Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 14.dp)
                .border(1.dp, edge, MaterialTheme.shapes.extraLarge),
            shape = MaterialTheme.shapes.extraLarge,
            color = glass,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Keep the app's own View menu immediately to the left of page navigation.
                ViewButton(editor)
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
            // This is the original app sidebar (Pages / Contents / Bookmarks / Search),
            // not the compact custom thumbnail popover that was replacing it.
            Row(
                Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 76.dp),
            ) {
                SidePanel(editor)
            }
        }
    }
}
