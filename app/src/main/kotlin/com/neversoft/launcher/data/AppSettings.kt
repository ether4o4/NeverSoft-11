package com.neversoft.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "launcher_settings")

object AppSettings {
    val KEY_LAUNCHER_THEME            = stringPreferencesKey("launcher_theme")
    val KEY_LAUNCHER_ORB_IMAGE        = stringPreferencesKey("launcher_orb_image")
    val KEY_LAUNCHER_WALLPAPER_IMAGE  = stringPreferencesKey("launcher_wallpaper_image")
    val KEY_LAUNCHER_DOCK_PINS        = stringPreferencesKey("launcher_dock_pins")
    val KEY_LAUNCHER_START_PINS       = stringPreferencesKey("launcher_start_pins")
    val KEY_FIRST_RUN                 = booleanPreferencesKey("first_run")
    val KEY_DESKTOP_ICON_POSITIONS    = stringPreferencesKey("desktop_icon_positions")
    val KEY_START_PINS_SEEDED         = booleanPreferencesKey("start_pins_seeded")
    val KEY_DESKTOP_ITEMS             = stringPreferencesKey("desktop_items")

    fun themeFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LAUNCHER_THEME] ?: "DARK" }

    fun dockPinsFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LAUNCHER_DOCK_PINS] ?: "[]" }

    fun startPinsFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LAUNCHER_START_PINS] ?: "[]" }

    fun startPinsSeededFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_START_PINS_SEEDED] ?: false }

    suspend fun setStartPinsSeeded(context: Context) =
        context.dataStore.edit { it[KEY_START_PINS_SEEDED] = true }

    // Custom Start button image (empty = default four-pane logo)
    fun orbImageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LAUNCHER_ORB_IMAGE] ?: "" }

    suspend fun setOrbImage(context: Context, path: String) =
        context.dataStore.edit { it[KEY_LAUNCHER_ORB_IMAGE] = path }

    // Custom wallpaper image (empty = procedural Bloom)
    fun wallpaperImageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LAUNCHER_WALLPAPER_IMAGE] ?: "" }

    suspend fun setWallpaperImage(context: Context, path: String) =
        context.dataStore.edit { it[KEY_LAUNCHER_WALLPAPER_IMAGE] = path }

    // Desktop icons: "" = never seeded, otherwise a JSON array of items
    fun desktopItemsFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_DESKTOP_ITEMS] ?: "" }

    suspend fun setDesktopItems(context: Context, json: String) =
        context.dataStore.edit { it[KEY_DESKTOP_ITEMS] = json }

    fun isFirstRunFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_FIRST_RUN] ?: true }

    suspend fun setTheme(context: Context, preset: String) =
        context.dataStore.edit { it[KEY_LAUNCHER_THEME] = preset }

    suspend fun setFirstRunComplete(context: Context) =
        context.dataStore.edit { it[KEY_FIRST_RUN] = false }

    suspend fun setDockPins(context: Context, json: String) =
        context.dataStore.edit { it[KEY_LAUNCHER_DOCK_PINS] = json }

    suspend fun setStartPins(context: Context, json: String) =
        context.dataStore.edit { it[KEY_LAUNCHER_START_PINS] = json }

    fun desktopIconPositionsFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_DESKTOP_ICON_POSITIONS] ?: "{}" }

    suspend fun setDesktopIconPositions(context: Context, json: String) =
        context.dataStore.edit { it[KEY_DESKTOP_ICON_POSITIONS] = json }
}
