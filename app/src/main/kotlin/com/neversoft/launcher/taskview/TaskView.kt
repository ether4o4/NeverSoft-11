package com.neversoft.launcher.taskview

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.neversoft.launcher.window.*

@Composable
fun TaskView(windows: List<ShellWindow>, onWindowFocus: (String) -> Unit,
    onWindowClose: (String) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.72f))
        .pointerInput(Unit) { detectTapGestures { onDismiss() } }) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(24.dp, 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Task View", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("${windows.size} open", color = Color.White.copy(0.6f), fontSize = 13.sp)
            }
            if (windows.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No open windows", color = Color.White.copy(0.5f)) }
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {
                    items(windows, key = { it.id }) { win ->
                        Box(Modifier.width(180.dp)) {
                            Card(modifier = Modifier.width(180.dp).height(200.dp)
                                .clickable { onWindowFocus(win.id) }
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = win.contentType.previewColor().copy(0.85f))) {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    Icon(toIcon(win.contentType), null, Modifier.size(48.dp), tint = Color.White.copy(0.4f))
                                    if (win.state == WindowState.MINIMIZED)
                                        Text("minimized", Modifier.align(Alignment.TopStart).padding(8.dp)
                                            .background(Color.Black.copy(0.5f), RoundedCornerShape(4.dp)).padding(4.dp, 2.dp),
                                            color = Color.White, fontSize = 10.sp)
                                }
                            }
                            Box(Modifier.align(Alignment.TopEnd).padding(6.dp).size(22.dp)
                                .background(Color(0xFFFF3B30), CircleShape).clickable { onWindowClose(win.id) },
                                Alignment.Center) { Text("✕", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            Text(win.title, Modifier.align(Alignment.BottomCenter).padding(top = 208.dp),
                                color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

fun WindowContentType.previewColor(): Color = when(this) {
    WindowContentType.FILE_EXPLORER  -> Color(0xFF1E4D8C)
    WindowContentType.CONTROL_PANEL -> Color(0xFF4A1E8C)
    WindowContentType.BROWSER       -> Color(0xFF8C4A1E)
    WindowContentType.TERMINAL      -> Color(0xFF1A1A1A)
    else                             -> Color(0xFF2A2A4A)
}
