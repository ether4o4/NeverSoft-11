package com.neversoft.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neversoft.launcher.data.AppSettings
import com.neversoft.launcher.onboarding.PermissionOnboardingScreen
import com.neversoft.launcher.shell.ShellScreen
import com.neversoft.launcher.theme.LocalLauncherTheme
import com.neversoft.launcher.theme.ThemeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            NeverSoftRoot()
        }
    }
}

@Composable
private fun NeverSoftRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeViewModel: ThemeViewModel = viewModel()

    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val currentPreset by themeViewModel.currentPreset.collectAsState()
    val isFirstRun by AppSettings.isFirstRunFlow(context).collectAsState(initial = true)

    MaterialTheme(typography = com.neversoft.launcher.theme.NsTypography) {
        CompositionLocalProvider(LocalLauncherTheme provides currentTheme) {
            if (isFirstRun) {
                PermissionOnboardingScreen(
                    onComplete = {
                        scope.launch {
                            AppSettings.setFirstRunComplete(context)
                        }
                    },
                )
            } else {
                ShellScreen(
                    selectedPreset = currentPreset,
                    onSelectPreset = themeViewModel::setThemePreset,
                )
            }
        }
    }
}
