package com.neversoft.launcher.shell

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/** Consume Back while Start is open; let the existing navigation handle it otherwise. */
@Composable
internal fun StartMenuBackHandler(visible: Boolean, onDismiss: () -> Unit) {
    BackHandler(enabled = visible, onBack = onDismiss)
}
