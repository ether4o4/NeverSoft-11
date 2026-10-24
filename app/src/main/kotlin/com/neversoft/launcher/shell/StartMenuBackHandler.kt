package com.neversoft.launcher.shell

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/** Consume Back while Start/Search is open; delegate normally once dismissed. */
@Composable
internal fun StartMenuBackHandler(visible: Boolean, onDismiss: () -> Unit) {
    BackHandler(enabled = visible, onBack = onDismiss)
}
