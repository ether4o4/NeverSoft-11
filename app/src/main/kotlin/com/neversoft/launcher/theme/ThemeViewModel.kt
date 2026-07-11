package com.neversoft.launcher.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neversoft.launcher.data.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _currentPreset = MutableStateFlow(ThemePreset.DARK)
    val currentPreset: StateFlow<ThemePreset> = _currentPreset

    val currentTheme: StateFlow<LauncherTheme> = _currentPreset
        .map { preset -> LauncherThemes.fromPreset(preset) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LauncherThemes.Dark,
        )

    init {
        viewModelScope.launch {
            AppSettings.themeFlow(context).collect { presetName ->
                // Legacy stored presets (GLASS etc.) fall back to Dark
                val preset = runCatching { ThemePreset.valueOf(presetName) }
                    .getOrDefault(ThemePreset.DARK)
                _currentPreset.value = preset
            }
        }
    }

    fun setThemePreset(preset: ThemePreset) {
        if (_currentPreset.value == preset) return
        _currentPreset.value = preset
        viewModelScope.launch {
            AppSettings.setTheme(context, preset.name)
        }
    }
}
