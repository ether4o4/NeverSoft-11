package com.neversoft.launcher.taskview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.window.ShellWindow
import com.neversoft.launcher.window.WindowContentType
import com.neversoft.launcher.window.WindowState
import com.neversoft.launcher.window.toIcon

// Windows 11 Task View: window thumbnails over the dimmed desktop, with the
// desktop switcher (Desktop 1 / Work) along the bottom like on PC.
@Composable
fun TaskView(
    windows: List<ShellWindow>,
    focusedWindowId: String?,
    onWindowFocus: (String) -> Unit,
    onWindowClose: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    currentDesktop: Int = 1,
    onSwitchDesktop: (Int) -> Unit = {},
    workLocked: Boolean = false,
) {
    val theme = LocalLauncherTheme.current
    Box(
        modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .pointerInput(Unit) { detectTapGestures { onDismiss() } },
    ) {
        if (windows.isEmpty()) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No open windows", color = Color.White, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text("Open an app to see it here", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(176.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(windows, key = { it.id }) { win ->
                    TaskViewCard(
                        window = win,
                        isFocused = win.id == focusedWindowId,
                        onFocus = { onWindowFocus(win.id) },
                        onClose = { onWindowClose(win.id) },
                    )
                }
            }
        }

        // Desktop switcher, bottom-center (like Win11's Task View)
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DesktopCard("Desktop 1", selected = currentDesktop == 1, locked = false) {
                onSwitchDesktop(1)
            }
            DesktopCard("Work", selected = currentDesktop == 2, locked = workLocked) {
                onSwitchDesktop(2)
            }
        }
    }
}

@Composable
private fun DesktopCard(label: String, selected: Boolean, locked: Boolean, onClick: () -> Unit) {
    val theme = LocalLauncherTheme.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(width = 96.dp, height = 58.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(theme.windowSurface.copy(alpha = 0.85f))
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) theme.accent else Color.White.copy(0.25f),
                    shape = RoundedCornerShape(6.dp),
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            if (locked) {
                Icon(Icons.Outlined.Lock, "Locked", Modifier.size(18.dp), tint = theme.textSecondary)
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(label, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun TaskViewCard(
    window: ShellWindow,
    isFocused: Boolean,
    onFocus: () -> Unit,
    onClose: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    Column {
        // Icon + title header
        Row(
            Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                toIcon(window.contentType), null, Modifier.size(14.dp),
                tint = if (window.contentType == WindowContentType.FILE_EXPLORER)
                    Color(0xFFFFCA28) else Color.White,
            )
            Spacer(Modifier.width(7.dp))
            Text(
                window.title, color = Color.White, fontSize = 12.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier.size(20.dp).clip(CircleShape)
                    .background(Color.White.copy(0.14f))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Close, "Close", Modifier.size(12.dp), tint = Color.White)
            }
        }
        // Thumbnail
        Box(
            Modifier
                .fillMaxWidth()
                .height(112.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(theme.windowSurface)
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) theme.accent else Color.White.copy(0.2f),
                    shape = RoundedCornerShape(6.dp),
                )
                .clickable { onFocus() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                toIcon(window.contentType), null, Modifier.size(36.dp),
                tint = if (window.contentType == WindowContentType.FILE_EXPLORER)
                    Color(0xFFFFCA28) else theme.textSecondary,
            )
            if (window.state == WindowState.MINIMIZED) {
                Text(
                    "Minimized",
                    color = theme.textSecondary, fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
                )
            }
        }
    }
}
