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
    val KEY_ICON_PACK                 = stringPreferencesKey("icon_pack")
    val KEY_WALLPAPER_FIT             = stringPreferencesKey("wallpaper_fit")
    val KEY_START_MENU_SIZE           = stringPreferencesKey("start_menu_size")
    val KEY_CALENDAR_SIZE             = stringPreferencesKey("calendar_size")
    val KEY_START_FOLDERS             = stringPreferencesKey("start_folders")
    val KEY_RECENT_OPENED_FILES       = stringPreferencesKey("recent_opened_files")
    val KEY_QUICK_APPS                = stringPreferencesKey("quick_apps")
    val KEY_APP_ICON_OVERRIDES        = stringPreferencesKey("app_icon_overrides")
    val KEY_START_ICON_SIZES          = stringPreferencesKey("start_icon_sizes")
    val KEY_DESKTOP_ITEMS_2           = stringPreferencesKey("desktop_items_2")
    val KEY_DESKTOP_ICON_POSITIONS_2  = stringPreferencesKey("desktop_icon_positions_2")
    val KEY_WORK_PIN                  = stringPreferencesKey("work_profile_pin")

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

    // Desktop icons: "" = never seeded, otherwise a JSON array of items.
    // Page 1 is the main desktop; page 2 is the "Work" desktop.
    fun desktopItemsFlow(context: Context, page: Int = 1): Flow<String> =
        context.dataStore.data.map {
            it[if (page == 2) KEY_DESKTOP_ITEMS_2 else KEY_DESKTOP_ITEMS] ?: ""
        }

    suspend fun setDesktopItems(context: Context, json: String, page: Int = 1) =
        context.dataStore.edit {
            it[if (page == 2) KEY_DESKTOP_ITEMS_2 else KEY_DESKTOP_ITEMS] = json
        }

    // Work-profile lock PIN ("" = not locked)
    fun workPinFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_WORK_PIN] ?: "" }

    suspend fun setWorkPin(context: Context, pin: String) =
        context.dataStore.edit { it[KEY_WORK_PIN] = pin }

    // Selected icon pack package ("" = system icons)
    fun iconPackFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_ICON_PACK] ?: "" }

    suspend fun setIconPack(context: Context, pkg: String) =
        context.dataStore.edit { it[KEY_ICON_PACK] = pkg }

    // Wallpaper fit: "crop" (fill screen, may trim edges) or "exact"
    // (scale to exactly the screen size, no cropping)
    fun wallpaperFitFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_WALLPAPER_FIT] ?: "crop" }

    suspend fun setWallpaperFit(context: Context, fit: String) =
        context.dataStore.edit { it[KEY_WALLPAPER_FIT] = fit }

    // Persisted panel sizes ("width,height" in dp; "" = default)
    fun startMenuSizeFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_START_MENU_SIZE] ?: "" }

    suspend fun setStartMenuSize(context: Context, size: String) =
        context.dataStore.edit { it[KEY_START_MENU_SIZE] = size }

    fun calendarSizeFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_CALENDAR_SIZE] ?: "" }

    suspend fun setCalendarSize(context: Context, size: String) =
        context.dataStore.edit { it[KEY_CALENDAR_SIZE] = size }

    // Start-menu folders: JSON array of {name, apps:[pkg,...]} (4 folders, <=4 apps each)
    fun startFoldersFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_START_FOLDERS] ?: "" }

    suspend fun setStartFolders(context: Context, json: String) =
        context.dataStore.edit { it[KEY_START_FOLDERS] = json }

    // Per-app custom icons: JSON object {packageName: imagePath} ("{}" = none)
    fun appIconOverridesFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_APP_ICON_OVERRIDES] ?: "{}" }

    suspend fun setAppIcon(context: Context, pkg: String, path: String) {
        context.dataStore.edit { prefs ->
            val obj = runCatching { org.json.JSONObject(prefs[KEY_APP_ICON_OVERRIDES] ?: "{}") }
                .getOrDefault(org.json.JSONObject())
            obj.put(pkg, path)
            prefs[KEY_APP_ICON_OVERRIDES] = obj.toString()
        }
    }

    suspend fun clearAppIcon(context: Context, pkg: String) {
        context.dataStore.edit { prefs ->
            val obj = runCatching { org.json.JSONObject(prefs[KEY_APP_ICON_OVERRIDES] ?: "{}") }
                .getOrDefault(org.json.JSONObject())
            obj.remove(pkg)
            prefs[KEY_APP_ICON_OVERRIDES] = obj.toString()
        }
    }

    // Per-app Start-menu pinned-tile size levels: JSON object {pkg: level 1..5}
    fun startIconSizesFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_START_ICON_SIZES] ?: "{}" }

    suspend fun setStartIconSize(context: Context, pkg: String, level: Int) {
        context.dataStore.edit { prefs ->
            val obj = runCatching { org.json.JSONObject(prefs[KEY_START_ICON_SIZES] ?: "{}") }
                .getOrDefault(org.json.JSONObject())
            obj.put(pkg, level)
            prefs[KEY_START_ICON_SIZES] = obj.toString()
        }
    }

    // Taskbar "Quick apps" folder: JSON array of package names (max 6)
    fun quickAppsFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_QUICK_APPS] ?: "[]" }

    suspend fun setQuickApps(context: Context, json: String) =
        context.dataStore.edit { it[KEY_QUICK_APPS] = json }

    // Recently-opened file paths, most recent first (capped)
    fun recentOpenedFilesFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_RECENT_OPENED_FILES] ?: "[]" }

    suspend fun addRecentOpenedFile(context: Context, path: String) {
        context.dataStore.edit { prefs ->
            val existing = runCatching {
                val arr = org.json.JSONArray(prefs[KEY_RECENT_OPENED_FILES] ?: "[]")
                MutableList(arr.length()) { arr.getString(it) }
            }.getOrDefault(mutableListOf())
            existing.remove(path)
            existing.add(0, path)
            while (existing.size > 30) existing.removeAt(existing.size - 1)
            prefs[KEY_RECENT_OPENED_FILES] = org.json.JSONArray(existing).toString()
        }
    }

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

    fun desktopIconPositionsFlow(context: Context, page: Int = 1): Flow<String> =
        context.dataStore.data.map {
            it[if (page == 2) KEY_DESKTOP_ICON_POSITIONS_2 else KEY_DESKTOP_ICON_POSITIONS] ?: "{}"
        }

    suspend fun setDesktopIconPositions(context: Context, json: String, page: Int = 1) =
        context.dataStore.edit {
            it[if (page == 2) KEY_DESKTOP_ICON_POSITIONS_2 else KEY_DESKTOP_ICON_POSITIONS] = json
        }
}
