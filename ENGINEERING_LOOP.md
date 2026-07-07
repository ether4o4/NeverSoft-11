# NeverSoft 11 — Engineering Loop Prompt
### Phase-locked build protocol · Model handoff guide · Loop completion gates

---

> **How to use this document**
> Copy the active phase block below and paste it in full as your prompt to the assigned model.
> Each block is self-contained — it tells the model exactly what to build, what files to read,
> what acceptance gates to verify, and what to tell you when the phase is done.
> At the end of each phase block the model will print a **PHASE COMPLETE** report
> and tell you exactly which model to switch to and what prompt to paste next.

---

## Master Context (prepend to every phase prompt)

```
=== NEVERSOFT 11 — MASTER CONTEXT ===

Project: NeverSoft 11 — near 1:1 Windows 11 OS shell replica for Android
Package: com.neversoft.launcher
minSdk: 29 | targetSdk: 35 | compileSdk: 35
Kotlin: 2.0.21 | Gradle: 8.11.1 | AGP: 8.7.3 | Java: 17
Repo: ether4o4/NeverSoft-11 (GitHub, main branch)
Build dir: /data/workspace/ns11-build/
Recovery reference: /data/workspace/NeverSoft-11/recovery/decompiled-src/
Source repos (read-only reference):
  - /data/workspace/ForTheWin11/          → DnD, desktop, window chrome
  - /data/workspace/NeverSoft-Services-OS/ → Theme engine, AppSettings, orb picker
  - /data/workspace/spotlight/             → SearchEngine + all providers
Spec: /data/workspace/neversoft-11-mashup-spec.md

RULES:
1. Never overwrite /data/workspace/NeverSoft-11/ (recovery only — read reference)
2. All new code goes in /data/workspace/ns11-build/
3. After every file write, confirm the path and line count
4. After every phase, run: git add . && git commit -m "Phase N: <summary>" && git push origin main
5. Use $GITHUB_TOKEN for all git operations
6. At phase end, print the PHASE COMPLETE block exactly as specified
```

---

## PHASE 0 — Clean Kotlin Compose Scaffold

**Assigned model: `gpt-5.3-codex`**
**Estimated time: 15–25 min**

---

```
=== PHASE 0 PROMPT — paste this to gpt-5.3-codex ===

[PREPEND MASTER CONTEXT ABOVE]

You are an elite Android launcher engineer. Execute Phase 0 of NeverSoft 11:
build the complete clean Kotlin Compose project scaffold from scratch.

GOAL: A fully structured Android project at /data/workspace/ns11-build/ that:
- Compiles without errors (validate syntax on every file you write)
- Has all engine stubs pre-carved so Phases 1–7 slot in cleanly
- Is pushed to ether4o4/NeverSoft-11 on GitHub

─── STEP 1: GitHub repo setup ───────────────────────────────────────────────
Check if ether4o4/NeverSoft-11 exists:
  curl -s -H "Authorization: token $GITHUB_TOKEN" \
    https://api.github.com/repos/ether4o4/NeverSoft-11 | python3 -c \
    "import sys,json; d=json.load(sys.stdin); print('EXISTS' if 'id' in d else 'MISSING')"

If MISSING, create it:
  curl -s -X POST -H "Authorization: token $GITHUB_TOKEN" \
    -H "Content-Type: application/json" \
    https://api.github.com/user/repos \
    -d '{"name":"NeverSoft-11","description":"NeverSoft 11 — Windows 11 OS shell replica for Android","private":false}'

─── STEP 2: Project directory structure ─────────────────────────────────────
Create EXACTLY this structure at /data/workspace/ns11-build/:

  ns11-build/
  ├── gradle/
  │   ├── libs.versions.toml
  │   └── wrapper/
  │       └── gradle-wrapper.properties
  ├── app/
  │   ├── build.gradle.kts
  │   └── src/main/
  │       ├── AndroidManifest.xml
  │       ├── res/
  │       │   ├── values/themes.xml
  │       │   ├── values/strings.xml
  │       │   ├── drawable/ic_launcher_background.xml
  │       │   └── mipmap-anydpi-v26/
  │       │       ├── ic_launcher.xml
  │       │       └── ic_launcher_round.xml
  │       └── kotlin/com/neversoft/launcher/
  │           ├── LauncherApp.kt
  │           ├── MainActivity.kt
  │           ├── receiver/BootReceiver.kt
  │           ├── theme/
  │           │   ├── LauncherTheme.kt    ← 10 presets
  │           │   ├── NsColors.kt
  │           │   └── Type.kt
  │           ├── data/AppSettings.kt     ← DataStore, all 5 keys
  │           ├── shell/ShellScreen.kt    ← root composable, all layers
  │           ├── taskbar/Taskbar.kt
  │           ├── startmenu/StartMenu.kt
  │           ├── desktop/Desktop.kt
  │           ├── window/
  │           │   ├── WindowManagerEngine.kt
  │           │   └── ShellWindowHost.kt
  │           ├── fileexplorer/FileExplorerWindow.kt
  │           ├── taskview/TaskView.kt
  │           └── search/SearchEngine.kt
  ├── build.gradle.kts
  ├── settings.gradle.kts
  └── README.md

─── STEP 3: Write every file ────────────────────────────────────────────────
Write each file completely. Do not use placeholders like "// TODO add content".
Every Kotlin file must be syntactically valid and compile.

Key requirements per file:

gradle/libs.versions.toml
  - agp=8.7.3, kotlin=2.0.21, compose-bom=2024.11.00
  - activity-compose=1.9.3, lifecycle=2.8.7, navigation-compose=2.8.4
  - datastore=1.1.1, coroutines=1.9.0, core-ktx=1.15.0
  - material-icons-extended, window=1.3.0
  - All deps as library aliases, all plugins as plugin aliases

app/build.gradle.kts
  - namespace="com.neversoft.launcher"
  - compileSdk=35, minSdk=29, targetSdk=35
  - compileOptions JavaVersion.VERSION_17
  - buildFeatures { compose = true }
  - All deps via libs.* aliases from version catalog

AndroidManifest.xml
  - CATEGORY_HOME + CATEGORY_DEFAULT in intent-filter (launcher replacement)
  - QUERY_ALL_PACKAGES, READ_CONTACTS, READ_MEDIA_IMAGES/VIDEO/AUDIO
  - MANAGE_EXTERNAL_STORAGE (tools:ignore="ScopedStorage")
  - PACKAGE_USAGE_STATS (for UsageStats-based most-used apps)
  - RECEIVE_BOOT_COMPLETED, VIBRATE
  - android:launchMode="singleTask" on MainActivity
  - BootReceiver registered

theme/LauncherTheme.kt
  - enum ThemePreset { GLASS, MACBOOK, RED, PURPLE, BLUE, GREEN, BLACK, DARK_GREY, LIGHT_GREY, WHITE }
  - data class LauncherTheme(preset, displayName, surfaceColor, surfaceAlpha, accentColor, onSurface, onAccent, blurRadius)
  - fun surfaceBrush(): Brush
  - object LauncherThemes with all 10 preset vals + all list + fromPreset()
  - Exact color values from MVE:
      Glass: surface=White alpha=0.12f accent=White
      MacBook: surface=Color(0xFFECECF1) alpha=0.92f accent=Color(0xFF0071E3) onSurface=Color(0xFF1D1D1F)
      Red: surface=Color(0xFFB41414) alpha=0.85f accent=Color(0xFFFF4444)
      Purple: surface=Color(0xFF5014A0) alpha=0.85f accent=Color(0xFFA855F7)
      Blue: surface=Color(0xFF143CB4) alpha=0.85f accent=Color(0xFF3B82F6)
      Green: surface=Color(0xFF14783C) alpha=0.85f accent=Color(0xFF22C55E)
      Black: surface=Color(0xFF0A0A0A) alpha=0.95f accent=White
      DarkGrey: surface=Color(0xFF282828) alpha=0.92f accent=Color(0xFF888888)
      LightGrey: surface=Color(0xFFDCDCDC) alpha=0.92f accent=Color(0xFF555555) onSurface=Color(0xFF111111)
      White: surface=White alpha=0.95f accent=Color(0xFF0071E3) onSurface=Color(0xFF111111)

data/AppSettings.kt
  - val Context.dataStore by preferencesDataStore("launcher_settings")
  - Keys: KEY_LAUNCHER_THEME (String), KEY_LAUNCHER_ORB_IMAGE (String),
          KEY_LAUNCHER_WALLPAPER_IMAGE (String), KEY_LAUNCHER_DOCK_PINS (String/JSON),
          KEY_LAUNCHER_START_PINS (String/JSON), KEY_FIRST_RUN (Boolean)
  - Flow accessors: themeFlow, dockPinsFlow, startPinsFlow, isFirstRunFlow
  - Suspend setters: setTheme, setFirstRunComplete, setDockPins, setStartPins

window/WindowManagerEngine.kt
  - enum WindowState { NORMAL, MINIMIZED, MAXIMIZED }
  - enum WindowContentType { FILE_EXPLORER, CONTROL_PANEL, BROWSER, TERMINAL, PHOTOS, MUSIC }
  - data class ShellWindow(id, title, contentType, state, position: Offset, size: DpSize, zIndex)
  - class WindowManagerEngine: mutableStateListOf<ShellWindow>
    - openWindow(contentType, title): ShellWindow — offsets each new window by 24f
    - focusWindow(id): bumps zIndex to front
    - closeWindow, minimizeWindow, maximizeWindow, restoreWindow, moveWindow

shell/ShellScreen.kt
  - Box filling screen with deep space radial gradient background
  - Layer 1: Desktop(modifier, recycleBindEnabled=true)
  - Layer 2: ShellWindowHost(engine, modifier)
  - Layer 3: if(taskViewVisible) TaskView(...)
  - Layer 4: if(startMenuVisible) StartMenu(...)
  - Layer 5: Taskbar(onOrbClick, onTaskViewClick, openWindows, onWindowTaskbarClick, modifier at BottomCenter height=60.dp)
  - startMenuVisible and taskViewVisible as mutableStateOf
  - windowEngine as remember { WindowManagerEngine() }

taskbar/Taskbar.kt
  - Frosted glass surface: background(brush) with shadow
  - Centered layout: [orb] [pinned apps + running indicators] [taskview btn] [clock] [tray]
  - Start orb: circular 44dp button, theme accent color, "NS" text bold
  - Clock: live via LaunchedEffect(Unit) { while(true) { update time; delay(1000) } }
  - Task View button: Icon(Icons.Default.TableChart or similar)
  - Running app dots: 4dp dot under each open window's icon
  - Parameters: onOrbClick, onTaskViewClick, openWindows: List<ShellWindow>, onWindowTaskbarClick, modifier

startmenu/StartMenu.kt
  - AnimatedVisibility with slideInVertically + fadeIn from bottom
  - Card 360dp wide, rounded 16dp, elevated glass surface
  - Search TextField at top, placeholder "Search apps, settings, documents…"
  - LazyVerticalGrid(cells=Fixed(4)) with 16 placeholder AppTile items
  - "See all ›" button — switches to full alpha list view (separate composable AllAppsView)
  - Bottom row: Row of 4 fixed IconButton items:
      ⚙ "Android Settings" → starts Intent(Settings.ACTION_SETTINGS)
      ⊞ "Control Panel"   → onOpenControlPanel()
      📁 "File Explorer"  → onOpenFileExplorer()
      >_ "Terminal"       → shows Snackbar "Coming soon"
  - Parameters: onDismiss, onOpenFileExplorer, onOpenControlPanel, modifier

desktop/Desktop.kt
  - Box with icon grid
  - Recycle Bin icon (Icons.Default.Delete, labeled "Recycle Bin") bottom-left
  - File Explorer icon (Icons.Default.Folder, labeled "File Explorer") top-left
  - 2–3 other placeholder desktop shortcut icons
  - Long-press on icon enters editMode (Phase 5 will expand this)

window/ShellWindowHost.kt
  - Box(Modifier.fillMaxSize())
  - For each window in engine.windows, sorted by zIndex:
    - if state != MINIMIZED: render ShellWindowCard
  - ShellWindowCard: Card with title bar + content placeholder
  - Title bar (48dp height): drag handle via pointerInput(detectDragGestures)
    - onDrag: engine.moveWindow(id, position + dragAmount)
    - Left: Icon + Text(title)
    - Right: IconButton(—) minimize, IconButton(⬜) maximize/restore, IconButton(✕) close
  - Content area: when(contentType) { FILE_EXPLORER -> FileExplorerWindow(); CONTROL_PANEL -> ControlPanelWindow(); else -> PlaceholderContent(title) }
  - Window positioned via Modifier.offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
  - Modifier.zIndex(window.zIndex.toFloat())

taskview/TaskView.kt
  - Full screen Box, background Color.Black.copy(alpha=0.72f)
  - "Task View" label top center
  - LazyRow of TaskCard(window) items
  - TaskCard: 160×200dp Card with colored placeholder + title below + ✕ button
  - Click card → onWindowFocus(id); dismiss TaskView
  - ✕ on card → onWindowClose(id)

fileexplorer/FileExplorerWindow.kt
  - Stub: Row { LeftNav(…) | ContentPane(…) }
  - LeftNav: Column of nav items (Home, Desktop, Documents, Downloads, Pictures, Music, Videos, This PC)
  - ContentPane: placeholder "Phase 4 will implement real file listing"
  - Both composables with correct parameters ready for Phase 4 expansion

search/SearchEngine.kt
  - Interface SearchProvider { suspend fun query(q: String): List<SearchResult> }
  - data class SearchResult(id, label, subtitle, type: ResultType, packageName, filePath)
  - enum ResultType { APP, FILE, MEDIA, CONTACT }
  - class SearchEngine(val providers: List<SearchProvider>) {
      suspend fun search(query: String): List<SearchResult> — stub returning emptyList()
    }
  - Phase 3 wires in real Spotlight providers

─── STEP 4: Resource stubs ──────────────────────────────────────────────────
res/values/themes.xml:
  <style name="Theme.NeverSoft11" parent="Theme.Material3.DayNight.NoActionBar"/>

res/values/strings.xml:
  <string name="app_name">NeverSoft 11</string>

res/drawable/ic_launcher_background.xml: solid black vector

res/mipmap-anydpi-v26/ic_launcher.xml and ic_launcher_round.xml:
  adaptive-icon with background=@drawable/ic_launcher_background and
  foreground monochrome vector (simple "NS" text path or solid white)

─── STEP 5: Gradle wrapper ──────────────────────────────────────────────────
gradle/wrapper/gradle-wrapper.properties:
  distributionUrl=https\://services.gradle.org/distributions/gradle-8.11.1-bin.zip

─── STEP 6: Git push ────────────────────────────────────────────────────────
cd /data/workspace/ns11-build
git init
git config user.email "build@neversoft.dev"
git config user.name "NeverSoft Builder"
git add .
git commit -m "Phase 0: Clean Kotlin Compose scaffold

- Package com.neversoft.launcher | minSdk 29 | Kotlin 2.0.21
- LauncherTheme 10 presets (Glass/MacBook/Red/Purple/Blue/Green/Black/DarkGrey/LightGrey/White)
- AppSettings DataStore with 5 keys
- WindowManagerEngine: ShellWindow model + full state management
- ShellScreen: all layers wired (desktop/windows/taskview/startmenu/taskbar)
- ShellWindowHost: draggable titled popup windows with z-order
- Taskbar: live clock, orb, pinned apps, running dots
- StartMenu: glass card, 4x4 grid, See all, bottom cluster 4 slots
- Desktop: Recycle Bin emblem + shortcut icons
- TaskView: dimmed overlay, horizontal cards
- All engine stubs pre-carved for Phases 1–7"
git branch -M main
git remote add origin https://$GITHUB_TOKEN@github.com/ether4o4/NeverSoft-11.git
git push -u origin main --force

─── STEP 7: Acceptance gates ────────────────────────────────────────────────
Verify before reporting done:
[ ] /data/workspace/ns11-build/gradle/libs.versions.toml exists and has all deps
[ ] AndroidManifest.xml has CATEGORY_HOME intent-filter
[ ] AndroidManifest.xml has MANAGE_EXTERNAL_STORAGE permission
[ ] LauncherTheme.kt has exactly 10 presets in LauncherThemes object
[ ] AppSettings.kt has all 5 keys as stringPreferencesKey/booleanPreferencesKey
[ ] WindowManagerEngine.kt compiles (no unresolved references)
[ ] ShellScreen.kt wires all 5 layers
[ ] ShellWindowHost.kt implements drag via pointerInput
[ ] Git push succeeded (print commit SHA)

─── PHASE 0 COMPLETION REPORT (print exactly this when done) ────────────────

══════════════════════════════════════════════════
  PHASE 0 COMPLETE ✓
══════════════════════════════════════════════════
Commit: [SHA]
Repo: https://github.com/ether4o4/NeverSoft-11
Files written: [N] files across [N] directories

Gates passed:
  ✓ libs.versions.toml — all deps present
  ✓ AndroidManifest — CATEGORY_HOME + all permissions
  ✓ LauncherTheme — 10 presets
  ✓ AppSettings — 5 keys + flows + setters
  ✓ WindowManagerEngine — ShellWindow + state ops
  ✓ ShellScreen — 5 layers wired
  ✓ ShellWindowHost — drag + z-order
  ✓ Git pushed to main

──────────────────────────────────────────────────
  NEXT: PHASE 1 — Theme Engine + Settings Persistence
  MODEL: gpt-5.3-codex (same — still implementation-heavy)
  ACTION: Copy the PHASE 1 block from ENGINEERING_LOOP.md and paste it
──────────────────────────────────────────────────
```

---

## PHASE 1 — Theme Engine + Settings Persistence

**Assigned model: `gpt-5.3-codex`**
**Reads from: Phase 0 scaffold**

---

```
=== PHASE 1 PROMPT — paste this to gpt-5.3-codex ===

[PREPEND MASTER CONTEXT ABOVE]

Phase 0 scaffold is complete at /data/workspace/ns11-build/.
Phase 1 goal: make the LauncherTheme system fully live — theme changes
propagate to every shell surface in real time.

─── STEP 1: ThemeViewModel ──────────────────────────────────────────────────
Create: shell/ThemeViewModel.kt

class ThemeViewModel(app: Application) : AndroidViewModel(app) {
    private val _theme = MutableStateFlow(LauncherThemes.Glass)
    val theme: StateFlow<LauncherTheme> = _theme.asStateFlow()

    init {
        viewModelScope.launch {
            AppSettings.themeFlow(app).collect { name ->
                _theme.value = LauncherThemes.fromPreset(
                    runCatching { ThemePreset.valueOf(name) }.getOrDefault(ThemePreset.GLASS)
                )
            }
        }
    }

    fun setTheme(preset: ThemePreset) {
        viewModelScope.launch {
            AppSettings.setTheme(getApplication(), preset.name)
        }
    }
}

─── STEP 2: CompositionLocal for theme ──────────────────────────────────────
In theme/LauncherTheme.kt add:
  val LocalLauncherTheme = compositionLocalOf { LauncherThemes.Glass }

─── STEP 3: Wire ThemeViewModel into ShellScreen ────────────────────────────
Update shell/ShellScreen.kt:
  val themeVm: ThemeViewModel = viewModel()
  val theme by themeVm.theme.collectAsState()
  CompositionLocalProvider(LocalLauncherTheme provides theme) {
      // existing Box content
  }

─── STEP 4: Apply theme to Taskbar ──────────────────────────────────────────
Update taskbar/Taskbar.kt:
  val theme = LocalLauncherTheme.current
  Use theme.surfaceBrush() as background
  Use theme.accentColor for orb button background
  Use theme.onSurface for text + icons

─── STEP 5: Apply theme to StartMenu ────────────────────────────────────────
Update startmenu/StartMenu.kt:
  val theme = LocalLauncherTheme.current
  Card background = theme.surfaceBrush()
  Search field colors = theme.onSurface
  Bottom cluster icons = theme.accentColor

─── STEP 6: Apply theme to ShellWindowHost ──────────────────────────────────
Update window/ShellWindowHost.kt title bar:
  background brush from theme.surfaceBrush()
  title text color = theme.onSurface
  buttons = theme.accentColor

─── STEP 7: ControlPanelWindow (live theme switcher) ────────────────────────
Create: window/ControlPanelWindow.kt

@Composable
fun ControlPanelWindow(onThemeSelected: (ThemePreset) -> Unit) {
    Column {
        Text("Theme Preset", style=MaterialTheme.typography.titleMedium)
        // 5-column grid of theme swatch buttons
        LazyVerticalGrid(columns = GridCells.Fixed(5)) {
            items(LauncherThemes.all) { t ->
                ThemeSwatchButton(theme = t, onSelected = { onThemeSelected(t.preset) })
            }
        }
        Spacer(16.dp)
        Text("Start Orb", style=MaterialTheme.typography.titleMedium)
        // OrbPickerSection — image picker button + preview
        OrbPickerSection()
    }
}

@Composable
fun ThemeSwatchButton(theme: LauncherTheme, onSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(theme.surfaceColor.copy(alpha = theme.surfaceAlpha))
            .border(2.dp, theme.accentColor, CircleShape)
            .clickable(onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        Text(theme.displayName.take(2), fontSize = 8.sp, color = theme.onSurface)
    }
}

Wire ControlPanelWindow into ShellWindowHost: when(CONTROL_PANEL) → ControlPanelWindow(onThemeSelected = { themeVm.setTheme(it) })
Pass themeVm down from ShellScreen or use viewModel() inside.

─── STEP 8: First-run persistence check ─────────────────────────────────────
In MainActivity.kt add an onboarding check:
  val isFirstRun by AppSettings.isFirstRunFlow(this).collectAsState(initial = true, lifecycleScope)
  If isFirstRun: show PermissionOnboardingScreen (new composable) before ShellScreen
  After user grants/dismisses all: AppSettings.setFirstRunComplete(this)

Create: shell/PermissionOnboardingScreen.kt
  - Full screen, dark background
  - NeverSoft 11 logo + "Setting up your desktop" title
  - List of 5 permissions with explanations:
      📦 All Packages — lets Start menu show your installed apps
      📁 Storage — powers File Explorer and file search
      🖼 Media — shows photos, music, and videos in search
      👤 Contacts — adds contact results to Spotlight search
      📊 Usage Stats — ranks your most-used apps in Start
  - "Grant All Permissions" primary button →
      ActivityResultLauncher for each permission in sequence
      MANAGE_EXTERNAL_STORAGE via Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION intent
  - "Skip for now" secondary button → mark first run complete, proceed
  - After all granted or skipped: navigate to ShellScreen

─── STEP 9: Git commit ──────────────────────────────────────────────────────
cd /data/workspace/ns11-build
git add .
git commit -m "Phase 1: Theme engine live + settings persistence + first-run onboarding

- ThemeViewModel: StateFlow<LauncherTheme> backed by DataStore
- LocalLauncherTheme CompositionLocal wires theme to all surfaces
- Taskbar, StartMenu, ShellWindowHost all themed
- ControlPanelWindow: 10 swatch picker + live theme switching
- PermissionOnboardingScreen: all 5 permissions with explanations
- First-run gate in MainActivity"
git push origin main

─── PHASE 1 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════
  PHASE 1 COMPLETE ✓
══════════════════════════════════════════════════
Gates:
  ✓ ThemeViewModel — StateFlow + DataStore persistence
  ✓ LocalLauncherTheme wired into ShellScreen
  ✓ Taskbar themed (surface brush + accent orb + onSurface text)
  ✓ StartMenu themed
  ✓ ShellWindowHost title bar themed
  ✓ ControlPanelWindow — 10 swatches, live switching
  ✓ PermissionOnboardingScreen — all 5 permissions
  ✓ First-run gate in MainActivity
  ✓ Git pushed

──────────────────────────────────────────────────
  NEXT: PHASE 2 — Window Manager Foundation
  MODEL: gpt-5.3-codex (still implementation — draggable windows, z-order polish)
  ACTION: Copy PHASE 2 block from ENGINEERING_LOOP.md and paste it
──────────────────────────────────────────────────
```

---

## PHASE 2 — Window Manager Foundation

**Assigned model: `gpt-5.3-codex`**
**Reads from: Phase 0 ShellWindowHost stub, Phase 1 theme wiring**

---

```
=== PHASE 2 PROMPT — paste this to gpt-5.3-codex ===

[PREPEND MASTER CONTEXT ABOVE]

Phases 0 and 1 complete. Phase 2 goal: fully working popup window system —
drag, resize hint, minimize/maximize/restore/close, proper z-order, taskbar
running state, and Task View wired to real window list.

─── STEP 1: Harden WindowManagerEngine ──────────────────────────────────────
Update window/WindowManagerEngine.kt:
- Add previousBounds: DpSize? and previousPosition: Offset? to ShellWindow
  (saves bounds before maximize so restore works)
- maximizeWindow: save current position+size as previous, set state=MAXIMIZED
- restoreWindow: if previous exists, restore bounds; set state=NORMAL
- Add toggleMinimize(id): if MINIMIZED → NORMAL else MINIMIZED
- Add reorderToFront(id): alias for focusWindow
- Add val activeWindowId: MutableState<String?> — tracks focused window

─── STEP 2: ShellWindowHost — production quality ────────────────────────────
Rewrite window/ShellWindowHost.kt completely:

@Composable
fun ShellWindowHost(engine: WindowManagerEngine, modifier: Modifier) {
    Box(modifier) {
        engine.windows
            .filter { it.state != WindowState.MINIMIZED }
            .sortedBy { it.zIndex }
            .forEach { window ->
                key(window.id) {
                    ShellWindowCard(
                        window = window,
                        onFocus = { engine.reorderToFront(window.id) },
                        onMove = { delta -> engine.moveWindow(window.id,
                            Offset(window.position.x + delta.x, window.position.y + delta.y)) },
                        onMinimize = { engine.toggleMinimize(window.id) },
                        onMaximizeRestore = {
                            if (window.state == WindowState.MAXIMIZED)
                                engine.restoreWindow(window.id)
                            else
                                engine.maximizeWindow(window.id)
                        },
                        onClose = { engine.closeWindow(window.id) },
                    )
                }
            }
    }
}

@Composable
fun ShellWindowCard(
    window: ShellWindow,
    onFocus: () -> Unit,
    onMove: (Offset) -> Unit,
    onMinimize: () -> Unit,
    onMaximizeRestore: () -> Unit,
    onClose: () -> Unit,
) {
    val theme = LocalLauncherTheme.current
    val isMaximized = window.state == WindowState.MAXIMIZED

    val offsetModifier = if (isMaximized) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .offset { IntOffset(window.position.x.roundToInt(), window.position.y.roundToInt()) }
            .size(window.size)
    }

    Card(
        modifier = offsetModifier
            .zIndex(window.zIndex.toFloat())
            .pointerInput(Unit) { detectTapGestures { onFocus() } }
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(Modifier.fillMaxSize().background(theme.surfaceBrush())) {
            // Title bar — draggable
            WindowTitleBar(
                window = window,
                onDrag = onMove,
                onFocus = onFocus,
                onMinimize = onMinimize,
                onMaximizeRestore = onMaximizeRestore,
                onClose = onClose,
                theme = theme,
            )
            // Content
            Box(Modifier.fillMaxSize()) {
                WindowContent(window)
            }
        }
    }
}

@Composable
fun WindowTitleBar(
    window: ShellWindow, onDrag: (Offset)->Unit, onFocus: ()->Unit,
    onMinimize: ()->Unit, onMaximizeRestore: ()->Unit, onClose: ()->Unit,
    theme: LauncherTheme
) {
    val isMaximized = window.state == WindowState.MAXIMIZED
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(theme.accentColor.copy(alpha = 0.18f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onFocus() },
                    onDrag = { _, dragAmount -> if (!isMaximized) onDrag(dragAmount) }
                )
            }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(window.contentType.icon(), contentDescription = null,
            modifier = Modifier.size(16.dp), tint = theme.onSurface)
        Spacer(Modifier.width(6.dp))
        Text(window.title, style = MaterialTheme.typography.labelMedium,
            color = theme.onSurface, modifier = Modifier.weight(1f))
        // Minimize
        WindowControlButton("—", theme.onSurface) { onMinimize() }
        // Maximize / Restore
        WindowControlButton(if (isMaximized) "❐" else "□", theme.onSurface) { onMaximizeRestore() }
        // Close
        WindowControlButton("✕", Color(0xFFFF4444)) { onClose() }
    }
}

// Helper extension
fun WindowContentType.icon(): ImageVector = when(this) {
    WindowContentType.FILE_EXPLORER -> Icons.Default.Folder
    WindowContentType.CONTROL_PANEL -> Icons.Default.Settings
    WindowContentType.BROWSER -> Icons.Default.Language
    WindowContentType.TERMINAL -> Icons.Default.Terminal
    else -> Icons.Default.Window
}

─── STEP 3: Taskbar running indicators ──────────────────────────────────────
Update taskbar/Taskbar.kt:
- For each open window in openWindows, show its icon in the pinned/running row
- Active indicator: 4dp wide 2dp tall rounded bar below icon, accentColor
- Clicking a running app's taskbar button → onWindowTaskbarClick(id)
- If window is minimized, clicking restores it
- Minimized windows still show their dot indicator (dimmed)

─── STEP 4: Task View real implementation ───────────────────────────────────
Update taskview/TaskView.kt:
- Receives windows: List<ShellWindow>
- Shows ALL windows (including minimized) as cards
- Minimized windows show a "minimized" badge on their card
- Smooth enter: fadeIn + scaleIn(initialScale=0.95f)
- Smooth exit: fadeOut + scaleOut

─── STEP 5: Window open animations ──────────────────────────────────────────
In ShellWindowCard wrap with AnimatedVisibility:
  enter = scaleIn(initialScale=0.85f) + fadeIn(tween(180))
  exit = scaleOut(targetScale=0.85f) + fadeOut(tween(140))

─── STEP 6: Git commit ──────────────────────────────────────────────────────
git add . && git commit -m "Phase 2: Window manager production quality

- ShellWindowHost: sorted z-order, key-stable recomposition
- ShellWindowCard: drag via detectDragGestures, maximize/restore with bounds save
- WindowTitleBar: themed, draggable, all 3 controls
- Taskbar: real running indicators + minimize restore
- TaskView: all windows including minimized, animated enter/exit
- Window open/close scale+fade animations"
git push origin main

─── PHASE 2 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════
  PHASE 2 COMPLETE ✓
══════════════════════════════════════════════════
Gates:
  ✓ Windows drag smoothly via detectDragGestures
  ✓ Maximize fills screen above taskbar; restore returns saved bounds
  ✓ Minimize hides window; taskbar indicator stays
  ✓ Z-order: click any window → comes to front
  ✓ Task View shows all open windows as animated cards
  ✓ Window open/close has scale+fade animation
  ✓ Git pushed

──────────────────────────────────────────────────
  NEXT: PHASE 3 — Start Menu + Spotlight Internal Module
  MODEL: SWITCH TO claude-opus-4.8
  REASON: Spotlight merger requires multi-package reasoning across 4 provider
          files. Opus 4.8 is the strongest model for cross-file system wiring.
  ACTION: Copy PHASE 3 block from ENGINEERING_LOOP.md and paste it to claude-opus-4.8
──────────────────────────────────────────────────
```

---

## PHASE 3 — Start Menu + Spotlight Internal Module

**Assigned model: `claude-opus-4.8`**
**Reads from: /data/workspace/spotlight/ — SearchEngine.kt, all providers**

---

```
=== PHASE 3 PROMPT — paste this to claude-opus-4.8 ===

[PREPEND MASTER CONTEXT ABOVE]

Phases 0–2 complete. Phase 3 goal: fully working Start menu — live Spotlight
search, 4×4 pinned+usage app grid, pin/unpin context menu, See All, confirmed
bottom cluster. Spotlight engine merged from ether4o4/spotlight into the
launcher package.

─── STEP 1: Read Spotlight source ───────────────────────────────────────────
Read these files completely before writing any code:
  /data/workspace/spotlight/app/src/main/java/com/neversoft/spotlight/SearchEngine.kt
  /data/workspace/spotlight/app/src/main/java/com/neversoft/spotlight/AppSearchProvider.kt
  /data/workspace/spotlight/app/src/main/java/com/neversoft/spotlight/FileSearchProvider.kt
  /data/workspace/spotlight/app/src/main/java/com/neversoft/spotlight/MediaSearchProvider.kt
  /data/workspace/spotlight/app/src/main/java/com/neversoft/spotlight/ContactSearchProvider.kt

Map out: what each provider needs (Context, permissions), what it returns,
how SearchEngine aggregates and dedupes them.

─── STEP 2: Port providers to launcher package ──────────────────────────────
For each provider, create a counterpart at:
  search/providers/AppSearchProvider.kt    → package com.neversoft.launcher.search.providers
  search/providers/FileSearchProvider.kt
  search/providers/MediaSearchProvider.kt
  search/providers/ContactSearchProvider.kt

Rules:
- Change package to com.neversoft.launcher.search.providers
- Remove any Android View / RecyclerView / Activity references
- Return List<SearchResult> (the data class in search/SearchEngine.kt)
- Keep the same query logic — do not simplify the actual search algorithm
- FileSearchProvider: keep the 2500ms budget walk + MediaStore fallback
- AppSearchProvider: use PackageManager.getInstalledApplications + UsageStatsManager
  for usage ranking (UsageStats-based sort for most-used apps)

─── STEP 3: Rewrite SearchEngine.kt ─────────────────────────────────────────
Replace the stub at search/SearchEngine.kt with the full implementation:

class LauncherSearchEngine(private val context: Context) {
    private val providers = listOf(
        AppSearchProvider(context),
        FileSearchProvider(context),
        MediaSearchProvider(context),
        ContactSearchProvider(context),
    )

    suspend fun search(query: String, filter: SearchFilter = SearchFilter.ALL): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        return withContext(Dispatchers.IO) {
            providers
                .filter { it.supports(filter) }
                .map { async { runCatching { it.query(query) }.getOrDefault(emptyList()) } }
                .awaitAll()
                .flatten()
                .distinctBy { it.id }
                .sortedByDescending { it.relevanceScore }
                .take(20)
        }
    }

    suspend fun getMostUsedApps(limit: Int = 16): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            (providers.firstOrNull { it is AppSearchProvider } as? AppSearchProvider)
                ?.getMostUsed(limit) ?: emptyList()
        }
    }
}

enum class SearchFilter { ALL, APPS, FILES, MEDIA, CONTACTS }

─── STEP 4: SearchViewModel ─────────────────────────────────────────────────
Create startmenu/SearchViewModel.kt:

class SearchViewModel(app: Application) : AndroidViewModel(app) {
    private val engine = LauncherSearchEngine(app)

    val query = MutableStateFlow("")
    val results: StateFlow<List<SearchResult>> = query
        .debounce(200)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList())
            else flow { emit(engine.search(q)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mostUsedApps: StateFlow<List<SearchResult>> = flow {
        emit(engine.getMostUsedApps(16))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setQuery(q: String) { query.value = q }
}

─── STEP 5: PinsViewModel ───────────────────────────────────────────────────
Create startmenu/PinsViewModel.kt:

class PinsViewModel(app: Application) : AndroidViewModel(app) {
    private val _startPins = MutableStateFlow<List<String>>(emptyList()) // package names
    private val _dockPins = MutableStateFlow<List<String>>(emptyList())
    val startPins: StateFlow<List<String>> = _startPins.asStateFlow()
    val dockPins: StateFlow<List<String>> = _dockPins.asStateFlow()

    init {
        viewModelScope.launch {
            AppSettings.startPinsFlow(app).collect { json ->
                _startPins.value = parseJsonArray(json)
            }
        }
        viewModelScope.launch {
            AppSettings.dockPinsFlow(app).collect { json ->
                _dockPins.value = parseJsonArray(json)
            }
        }
    }

    fun pinToStart(pkg: String) = updateStartPins(_startPins.value + pkg)
    fun unpinFromStart(pkg: String) = updateStartPins(_startPins.value - pkg)
    fun pinToDock(pkg: String) = updateDockPins(_dockPins.value + pkg)
    fun unpinFromDock(pkg: String) = updateDockPins(_dockPins.value - pkg)
    fun reorderStartPins(from: Int, to: Int) { /* swap and persist */ }

    private fun updateStartPins(list: List<String>) {
        viewModelScope.launch {
            AppSettings.setStartPins(app, toJsonArray(list.distinct()))
        }
    }
    private fun updateDockPins(list: List<String>) {
        viewModelScope.launch {
            AppSettings.setDockPins(app, toJsonArray(list.distinct()))
        }
    }

    private fun parseJsonArray(json: String): List<String> =
        runCatching { org.json.JSONArray(json).let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        }}.getOrDefault(emptyList())

    private fun toJsonArray(list: List<String>): String =
        org.json.JSONArray(list).toString()
}

─── STEP 6: Rewrite StartMenu.kt — full production version ──────────────────
Rewrite startmenu/StartMenu.kt completely:

@Composable
fun StartMenu(
    onDismiss: () -> Unit,
    onOpenFileExplorer: () -> Unit,
    onOpenControlPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchVm: SearchViewModel = viewModel()
    val pinsVm: PinsViewModel = viewModel()
    val theme = LocalLauncherTheme.current

    val query by searchVm.query.collectAsState()
    val results by searchVm.results.collectAsState()
    val mostUsed by searchVm.mostUsedApps.collectAsState()
    val startPins by pinsVm.startPins.collectAsState()

    var showAllApps by remember { mutableStateOf(false) }
    var contextMenuApp by remember { mutableStateOf<SearchResult?>(null) }

    // Dismiss on outside tap
    Box(Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() }) {
        AnimatedContent(targetState = showAllApps) { allApps ->
            if (allApps) {
                AllAppsView(
                    onBack = { showAllApps = false },
                    onAppClick = { /* launch app */ },
                    onLongPress = { app -> contextMenuApp = app },
                    modifier = modifier
                )
            } else {
                StartMenuPanel(
                    query = query,
                    onQueryChange = searchVm::setQuery,
                    results = results,
                    mostUsed = mostUsed,
                    startPins = startPins,
                    onSeeAll = { showAllApps = true },
                    onAppLongPress = { app -> contextMenuApp = app },
                    onOpenFileExplorer = onOpenFileExplorer,
                    onOpenControlPanel = onOpenControlPanel,
                    theme = theme,
                    modifier = modifier
                )
            }
        }
    }

    // Context menu
    contextMenuApp?.let { app ->
        AppContextMenu(
            app = app,
            isPinnedToStart = startPins.contains(app.packageName),
            onPinToStart = { pinsVm.pinToStart(app.packageName!!); contextMenuApp = null },
            onUnpinFromStart = { pinsVm.unpinFromStart(app.packageName!!); contextMenuApp = null },
            onPinToDock = { pinsVm.pinToDock(app.packageName!!); contextMenuApp = null },
            onDismiss = { contextMenuApp = null }
        )
    }
}

The grid in StartMenuPanel:
- 16 slots total
- Slots 0..(startPins.size-1): pinned apps (show pin badge ⊞ in corner)
- Remaining slots: mostUsed apps not already pinned
- If query is not blank: replace grid with SearchResultsList(results)
- Each AppTile: 64dp × 72dp, icon + label below, long-press → context menu

AppContextMenu: DropdownMenu with items:
  - "Open"
  - "Pin to Start" / "Unpin from Start" (toggle)
  - "Pin to taskbar" / "Unpin from taskbar" (toggle)

─── STEP 7: Wire PinsViewModel into Taskbar ─────────────────────────────────
In Taskbar.kt:
  val pinsVm: PinsViewModel = viewModel()
  val dockPins by pinsVm.dockPins.collectAsState()
  Render dockPins as icon buttons in taskbar center row
  Long-press on pinned taskbar icon → DropdownMenu → "Unpin from taskbar"

─── STEP 8: Launch apps from Start menu results ─────────────────────────────
Create util/AppLauncher.kt:
  fun launchApp(context: Context, packageName: String) {
      context.packageManager.getLaunchIntentForPackage(packageName)
          ?.also { context.startActivity(it) }
  }

─── STEP 9: Git commit ──────────────────────────────────────────────────────
git add . && git commit -m "Phase 3: Start menu + Spotlight internal module

- All 4 Spotlight providers ported to com.neversoft.launcher.search.providers
- LauncherSearchEngine: parallel search, dedup, relevance sort, 200ms debounce
- SearchViewModel: live query flow, mostUsedApps from UsageStats
- PinsViewModel: startPins + dockPins with DataStore persistence
- StartMenu: search bar, 4x4 grid (pinned first + usage fill), See All
- AllAppsView: alphabetical full app list
- AppContextMenu: Pin to Start / Pin to taskbar / Open
- Taskbar: dockPins rendered as permanent icon buttons
- AppLauncher utility"
git push origin main

─── PHASE 3 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════
  PHASE 3 COMPLETE ✓
══════════════════════════════════════════════════
Gates:
  ✓ All 4 Spotlight providers ported — package renamed, View refs removed
  ✓ LauncherSearchEngine: parallel coroutine search, dedup, ranked
  ✓ SearchViewModel: debounced query flow
  ✓ PinsViewModel: start + dock pins with DataStore persistence
  ✓ StartMenu: search live-filters grid; pinned apps first
  ✓ AppContextMenu: pin/unpin to Start and taskbar
  ✓ Taskbar shows dock pins
  ✓ Git pushed

──────────────────────────────────────────────────
  NEXT: PHASE 4 — File Explorer (fully operational)
  MODEL: SWITCH TO gpt-5.3-codex
  REASON: Phase 4 is pure implementation — file system queries, MediaStore,
          UI layout. Codex is faster and more accurate on this kind of work.
  ACTION: Copy PHASE 4 block from ENGINEERING_LOOP.md and paste it to gpt-5.3-codex
──────────────────────────────────────────────────
```

---

## PHASE 4 — File Explorer (Fully Operational)

**Assigned model: `gpt-5.3-codex`**

---

```
=== PHASE 4 PROMPT — paste this to gpt-5.3-codex ===

[PREPEND MASTER CONTEXT ABOVE]

Phases 0–3 complete. Phase 4: fully operational File Explorer popup window
matching the ForTheWin screenshot — real folder navigation, real file listing,
Windows-style left nav + right pane, themed window chrome.

─── Reference screenshot ────────────────────────────────────────────────────
Target layout from ForTheWin screenshot (uploaded file):
  Left nav (fixed 160dp): Home, Desktop, Documents, Downloads, Pictures, Music, Videos, This PC
  Right pane: Toolbar row + path breadcrumb + file grid/list
  Toolbar: New Folder, Share, Delete (greyed when nothing selected), search box
  Home view: Quick access section, Favorites section, Recent files section

─── STEP 1: FileExplorerViewModel ───────────────────────────────────────────
Create fileexplorer/FileExplorerViewModel.kt:

class FileExplorerViewModel(app: Application) : AndroidViewModel(app) {
    private val _currentPath = MutableStateFlow<File?>(null)
    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    private val _selectedFiles = MutableStateFlow<Set<String>>(emptySet())
    private val _navSection = MutableStateFlow(NavSection.HOME)

    val currentPath = _currentPath.asStateFlow()
    val files = _files.asStateFlow()
    val selectedFiles = _selectedFiles.asStateFlow()
    val navSection = _navSection.asStateFlow()

    fun navigateTo(section: NavSection) {
        _navSection.value = section
        _currentPath.value = section.toFile(getApplication())
        loadFiles(section.toFile(getApplication()))
    }

    fun navigateToFile(file: File) {
        if (file.isDirectory) {
            _currentPath.value = file
            loadFiles(file)
        }
        // files: open with intent (Phase 7 polish)
    }

    private fun loadFiles(dir: File?) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = dir?.listFiles()
                ?.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
                ?.map { FileItem(it) }
                ?: emptyList()
            _files.value = items
        }
    }
}

enum class NavSection(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    DESKTOP("Desktop", Icons.Default.DesktopWindows),
    DOCUMENTS("Documents", Icons.Default.Description),
    DOWNLOADS("Downloads", Icons.Default.Download),
    PICTURES("Pictures", Icons.Default.Image),
    MUSIC("Music", Icons.Default.MusicNote),
    VIDEOS("Videos", Icons.Default.VideoLibrary),
    THIS_PC("This PC", Icons.Default.Computer);

    fun toFile(context: Context): File? = when(this) {
        HOME -> null
        DESKTOP -> Environment.getExternalStoragePublicDirectory("Desktop")
        DOCUMENTS -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        DOWNLOADS -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        PICTURES -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        MUSIC -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        VIDEOS -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        THIS_PC -> Environment.getExternalStorageDirectory()
    }
}

data class FileItem(val file: File) {
    val name: String get() = file.name
    val isDirectory: Boolean get() = file.isDirectory
    val size: Long get() = file.length()
    val lastModified: Long get() = file.lastModified()
    val extension: String get() = file.extension.lowercase()
}

─── STEP 2: Rewrite FileExplorerWindow.kt ───────────────────────────────────
Full implementation matching ForTheWin screenshot layout exactly:

@Composable
fun FileExplorerWindow() {
    val vm: FileExplorerViewModel = viewModel()
    val theme = LocalLauncherTheme.current
    val navSection by vm.navSection.collectAsState()
    val files by vm.files.collectAsState()
    val currentPath by vm.currentPath.collectAsState()

    Row(Modifier.fillMaxSize()) {
        // Left nav — 160dp fixed
        FileExplorerLeftNav(
            selectedSection = navSection,
            onSectionSelected = { vm.navigateTo(it) },
            theme = theme,
            modifier = Modifier.width(160.dp).fillMaxHeight()
        )
        // Divider
        Divider(modifier = Modifier.width(1.dp).fillMaxHeight(),
            color = theme.accentColor.copy(alpha = 0.2f))
        // Right pane
        Column(Modifier.weight(1f).fillMaxHeight()) {
            FileExplorerToolbar(theme = theme)
            if (navSection == NavSection.HOME) {
                FileExplorerHomePane(theme = theme)
            } else {
                FileExplorerFilePane(
                    files = files,
                    currentPath = currentPath,
                    onFileClick = { vm.navigateToFile(it.file) },
                    theme = theme
                )
            }
        }
    }
}

FileExplorerLeftNav:
  - Column with vertical scroll
  - Header: "NeverSoft 11" small label
  - NavSection.entries.forEach → NavItem(section, isSelected, onClick)
  - NavItem: Row(icon + label), highlight accentColor.copy(0.15f) when selected

FileExplorerToolbar:
  - Row: search TextField (flex), New Folder btn, Share btn (greyed), Delete btn (greyed)
  - Height 40dp

FileExplorerHomePane:
  - LazyColumn with 3 sections:
    - "Quick access" header + 4 folder chips (Documents, Downloads, Pictures, Music)
    - "Favorites" header + 2 placeholder items
    - "Recent" header + if MANAGE_EXTERNAL_STORAGE granted: MediaStore recent files query
      else: "Grant storage access to see recent files" with one-tap button

FileExplorerFilePane:
  - Breadcrumb bar: path segments as clickable Text chips
  - if files.isEmpty(): "This folder is empty"
  - else: LazyVerticalGrid(columns=Fixed(4)) of FileGridItem
  - FileGridItem: 72×80dp, folder icon or file type icon + name label truncated

─── STEP 3: File type icons ─────────────────────────────────────────────────
Create fileexplorer/FileIcons.kt:
fun fileIcon(item: FileItem): ImageVector = when {
    item.isDirectory -> Icons.Default.Folder
    item.extension in listOf("jpg","jpeg","png","webp","gif") -> Icons.Default.Image
    item.extension in listOf("mp4","mkv","avi","mov") -> Icons.Default.VideoFile
    item.extension in listOf("mp3","m4a","flac","ogg","wav") -> Icons.Default.AudioFile
    item.extension == "pdf" -> Icons.Default.PictureAsPdf
    item.extension in listOf("zip","tar","gz","rar") -> Icons.Default.FolderZip
    item.extension in listOf("apk") -> Icons.Default.Android
    else -> Icons.Default.InsertDriveFile
}

─── STEP 4: Permission inline re-prompt ─────────────────────────────────────
In FileExplorerHomePane and FileExplorerFilePane:
  val hasStorage = remember {
      Environment.isExternalStorageManager() ||
      ContextCompat.checkSelfPermission(ctx, READ_EXTERNAL_STORAGE) == GRANTED
  }
  If !hasStorage: show Banner with "Grant storage access" button →
    startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:$packageName")))

─── STEP 5: Git commit ──────────────────────────────────────────────────────
git add . && git commit -m "Phase 4: File Explorer fully operational

- FileExplorerViewModel: real folder navigation + file listing via File API
- NavSection enum maps to real Android directories
- FileExplorerLeftNav: all 8 sections, active highlight, themed
- FileExplorerToolbar: search + New/Share/Delete controls
- FileExplorerHomePane: Quick access, Favorites, Recent via MediaStore
- FileExplorerFilePane: breadcrumb + LazyVerticalGrid + file type icons
- Inline storage permission re-prompt (no redirect to Settings)"
git push origin main

─── PHASE 4 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════
  PHASE 4 COMPLETE ✓
══════════════════════════════════════════════════
Gates:
  ✓ Left nav 8 sections navigate to real device directories
  ✓ File grid lists real files with type icons
  ✓ Breadcrumb navigates back up path
  ✓ Home pane: Quick access + Recent via MediaStore
  ✓ Inline permission re-prompt when storage missing
  ✓ Themed window chrome matches other windows
  ✓ Git pushed

──────────────────────────────────────────────────
  NEXT: PHASE 5 — Desktop DnD + Recycle Bin Emblem
  MODEL: gpt-5.3-codex (same)
  ACTION: Copy PHASE 5 block and paste to gpt-5.3-codex
──────────────────────────────────────────────────
```

---

## PHASE 5 — Desktop DnD + Recycle Bin Emblem

**Assigned model: `gpt-5.3-codex`**
**Reads from: /data/workspace/ForTheWin11/.../DesktopManager.kt**

---

```
=== PHASE 5 PROMPT — paste this to gpt-5.3-codex ===

[PREPEND MASTER CONTEXT ABOVE]

Phase 5: Port ForTheWin11's desktop drag/drop + app removal system into
Compose-native implementation.

─── STEP 1: Read FTW source ─────────────────────────────────────────────────
Read completely:
  /data/workspace/ForTheWin11/app/src/main/java/com/example/forthewin/ui/controllers/DesktopManager.kt

Map: setEditMode(), removeIcon(), startWiggle(), stopWiggle(),
drag threshold (12dp), wiggle range (-3f to 3f at 150ms).
Port all behavior — do not simplify.

─── STEP 2: DesktopViewModel ────────────────────────────────────────────────
Create desktop/DesktopViewModel.kt:
- _icons: mutableStateListOf<DesktopIcon>
- _editMode: MutableStateFlow<Boolean>
- enterEditMode() / exitEditMode()
- removeIcon(id)
- reorderIcon(fromIndex, toIndex)
- persists icon list via AppSettings (add KEY_DESKTOP_ICONS key)

data class DesktopIcon(
    val id: String,
    val packageName: String,
    val label: String,
    val isSystem: Boolean = false, // for Recycle Bin, can't remove
)

─── STEP 3: Rewrite Desktop.kt — production DnD ─────────────────────────────
Full implementation:

@Composable
fun Desktop(modifier: Modifier) {
    val vm: DesktopViewModel = viewModel()
    val icons by vm.icons
    val editMode by vm.editMode.collectAsState()

    // Exit edit mode on back gesture
    BackHandler(editMode) { vm.exitEditMode() }

    Box(modifier) {
        DesktopIconGrid(
            icons = icons,
            editMode = editMode,
            onLongPress = { vm.enterEditMode() },
            onRemove = { vm.removeIcon(it) },
            onReorder = { from, to -> vm.reorderIcon(from, to) },
            onIconTap = { if (!editMode) launchApp(context, it.packageName) }
        )

        // Edit mode overlay hint
        if (editMode) {
            Text("Tap outside to exit", Modifier.align(Alignment.TopCenter).padding(top=8.dp),
                color=Color.White.copy(alpha=0.7f), fontSize=12.sp)
        }
    }
}

DesktopIconGrid:
- LazyVerticalGrid or custom grid with reorder support
- Each icon: DesktopIconItem(icon, editMode, onRemove, onDragStart, ...)

DesktopIconItem:
- Wiggle animation when editMode:
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(editMode) {
        if (editMode) {
            while(true) {
                rotation.animateTo(3f, tween(150))
                rotation.animateTo(-3f, tween(150))
            }
        } else rotation.animateTo(0f, tween(100))
    }
    Modifier.graphicsLayer { rotationZ = rotation.value }

- Delete X badge (top-right corner, 20dp circle, red):
    AnimatedVisibility(editMode) {
        Box(Modifier.align(TopEnd).size(20.dp)
            .background(Color(0xFFFF3B30), CircleShape)
            .clickable { onRemove(icon.id) }) {
            Text("✕", color=White, fontSize=10.sp, Modifier.align(Center))
        }
    }

- Drag: pointerInput(editMode) {
    if (editMode) detectDragGesturesAfterLongPress(
        onDragStart = { ... },
        onDrag = { change, dragAmount -> ... },  // move icon
        onDragEnd = { ... } // snap to nearest grid cell, call onReorder
    )
  } else {
    detectTapGestures(onTap = { onIconTap() }, onLongPress = { onLongPress() })
  }

─── STEP 4: Recycle Bin emblem ──────────────────────────────────────────────
Add to DesktopViewModel default icons list at init:
  DesktopIcon(
      id = "recycle_bin",
      packageName = "",
      label = "Recycle Bin",
      isSystem = true // cannot be removed
  )

In DesktopIconItem, when packageName is empty and id == "recycle_bin":
  - Show Icons.Default.Delete icon (trash can)
  - No app launch on tap (show "Recycle Bin coming soon" snackbar)
  - No remove X badge (isSystem = true)

─── STEP 5: Git commit ──────────────────────────────────────────────────────
git add . && git commit -m "Phase 5: Desktop DnD + Recycle Bin emblem

- DesktopViewModel: icon list, edit mode, reorder, remove, DataStore persist
- DesktopIconItem: wiggle animation (±3f at 150ms), X delete badge
- Drag: detectDragGesturesAfterLongPress + snap-to-grid reorder
- Long press enters edit mode; back gesture exits
- Recycle Bin: permanent system icon on desktop, trash icon, isSystem flag"
git push origin main

─── PHASE 5 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════
  PHASE 5 COMPLETE ✓
══════════════════════════════════════════════════
Gates:
  ✓ Long-press enters edit mode
  ✓ Icons wiggle ±3° at 150ms interval
  ✓ X badge appears on each removable icon
  ✓ Drag reorders icons on desktop grid
  ✓ Remove deletes shortcut (not the app)
  ✓ Recycle Bin emblem present, non-removable
  ✓ Desktop layout persists via DataStore
  ✓ Git pushed

──────────────────────────────────────────────────
  NEXT: PHASE 6 — Task View
  MODEL: gpt-5.3-codex (same)
  ACTION: Copy PHASE 6 block and paste to gpt-5.3-codex
──────────────────────────────────────────────────
```

---

## PHASE 6 — Task View

**Assigned model: `gpt-5.3-codex`**

---

```
=== PHASE 6 PROMPT — paste this to gpt-5.3-codex ===

[PREPEND MASTER CONTEXT ABOVE]

Phase 6: Polish Task View into a premium, ForTheWin-quality implementation.

─── Reference ───────────────────────────────────────────────────────────────
ForTheWin Task View screenshot shows:
  - "Task View" label top-left
  - Desktop + taskbar dimmed/blurred behind
  - Horizontal scrolling cards, each with content preview + title below
  - Cards: ~180×220dp, rounded, slight shadow

─── STEP 1: Task View window preview thumbnails ──────────────────────────────
For each ShellWindow, generate a color-coded preview card:
  - File Explorer → blue tinted card with folder icon
  - Control Panel → purple tinted card with settings icon
  - Browser → orange tinted card with language icon
  - Terminal → dark card with terminal icon
  (Real screenshot capture is API 26+ and requires special setup — use colored
  previews for v1, real screenshots in Phase 7 polish)

─── STEP 2: Rewrite TaskView.kt — premium quality ────────────────────────────

@Composable
fun TaskView(
    windows: List<ShellWindow>,
    onWindowFocus: (String) -> Unit,
    onWindowClose: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalLauncherTheme.current

    Box(modifier.fillMaxSize()) {
        // Blurred/dimmed scrim
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.72f))
        }

        Column(Modifier.fillMaxSize()) {
            // Header
            Row(Modifier.fillMaxWidth().padding(24.dp, 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Task View", color=Color.White,
                    style=MaterialTheme.typography.headlineSmall,
                    fontWeight=FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("${windows.size} window${if(windows.size!=1) "s" else ""}",
                    color=Color.White.copy(alpha=0.6f), fontSize=14.sp)
            }

            if (windows.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No open windows", color=Color.White.copy(alpha=0.5f))
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal=24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(windows, key = { it.id }) { window ->
                        TaskViewCard(
                            window = window,
                            onClick = { onWindowFocus(window.id) },
                            onClose = { onWindowClose(window.id) },
                            theme = theme,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Tap outside cards to dismiss
        Box(Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures { onDismiss() }
        })
    }
}

@Composable
fun TaskViewCard(
    window: ShellWindow,
    onClick: () -> Unit,
    onClose: () -> Unit,
    theme: LauncherTheme
) {
    val isMinimized = window.state == WindowState.MINIMIZED
    Box(Modifier.width(180.dp).wrapContentHeight()) {
        Card(
            modifier = Modifier.width(180.dp).height(200.dp)
                .clickable(onClick=onClick)
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = window.contentType.previewColor().copy(alpha = 0.85f)
            )
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(window.contentType.icon(), contentDescription=null,
                    modifier=Modifier.size(48.dp), tint=Color.White.copy(alpha=0.4f))
                if (isMinimized) {
                    Box(Modifier.align(Alignment.TopStart).padding(8.dp)
                        .background(Color.Black.copy(0.5f), RoundedCornerShape(4.dp))
                        .padding(4.dp, 2.dp)) {
                        Text("minimized", color=Color.White, fontSize=10.sp)
                    }
                }
            }
        }
        // Close button
        Box(Modifier.align(Alignment.TopEnd).padding(6.dp)
            .size(22.dp).background(Color(0xFFFF3B30), CircleShape)
            .clickable { onClose() },
            contentAlignment = Alignment.Center) {
            Text("✕", color=Color.White, fontSize=11.sp, fontWeight=FontWeight.Bold)
        }
        // Title below card
        Text(window.title,
            modifier=Modifier.align(Alignment.BottomCenter).padding(bottom=(-20).dp),
            color=Color.White, fontSize=12.sp, maxLines=1,
            overflow=TextOverflow.Ellipsis)
    }
}

fun WindowContentType.previewColor(): Color = when(this) {
    WindowContentType.FILE_EXPLORER -> Color(0xFF1E4D8C)
    WindowContentType.CONTROL_PANEL -> Color(0xFF4A1E8C)
    WindowContentType.BROWSER -> Color(0xFF8C4A1E)
    WindowContentType.TERMINAL -> Color(0xFF1A1A1A)
    else -> Color(0xFF2A2A4A)
}

─── STEP 3: Enter/exit animation ────────────────────────────────────────────
Wrap Task View in AnimatedVisibility in ShellScreen:
  enter = fadeIn(tween(200)) + scaleIn(initialScale=0.96f, tween(200))
  exit = fadeOut(tween(160)) + scaleOut(targetScale=0.96f, tween(160))

─── STEP 4: Git commit ──────────────────────────────────────────────────────
git add . && git commit -m "Phase 6: Task View polished

- Full-screen dimmed overlay with task cards
- LazyRow horizontal scroll with window preview cards
- Color-coded previews per content type
- Minimized badge on minimized windows
- Close ✕ on each card
- Animated enter/exit (fade + scale)"
git push origin main

─── PHASE 6 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════
  PHASE 6 COMPLETE ✓
══════════════════════════════════════════════════
Gates:
  ✓ Task View dims desktop with 0.72 alpha scrim
  ✓ Horizontal scrolling window cards
  ✓ Color-coded content-type previews
  ✓ Minimized badge shown correctly
  ✓ Close ✕ removes window from Task View
  ✓ Animated enter/exit
  ✓ Git pushed

──────────────────────────────────────────────────
  NEXT: PHASE 7 — Theme Polish + APK Build
  MODEL: SWITCH TO claude-opus-4.8
  REASON: Phase 7 requires holistic cross-surface review — catching inconsistencies
          across all 7 phases that codex won't notice in isolation. Then APK build.
  ACTION: Copy PHASE 7 block and paste to claude-opus-4.8
──────────────────────────────────────────────────
```

---

## PHASE 7 — Theme Polish + Signed APK Build

**Assigned model: `claude-opus-4.8`**
**This is the final phase — deliverable is a downloadable APK**

---

```
=== PHASE 7 PROMPT — paste this to claude-opus-4.8 ===

[PREPEND MASTER CONTEXT ABOVE]

Phases 0–6 complete. Phase 7: full theme coverage audit, final polish,
then build a signed debug APK and publish it as a downloadable artifact.

─── STEP 1: Cross-surface theme audit ───────────────────────────────────────
Read every file that renders UI and verify it uses LocalLauncherTheme.current:
  shell/ShellScreen.kt
  taskbar/Taskbar.kt
  startmenu/StartMenu.kt
  window/ShellWindowHost.kt
  window/ControlPanelWindow.kt
  fileexplorer/FileExplorerWindow.kt
  taskview/TaskView.kt
  desktop/Desktop.kt

For each file check:
  [ ] Title bar / chrome uses theme.surfaceBrush() or theme.surfaceColor
  [ ] Text colors use theme.onSurface (not hardcoded Color.White or Color.Black)
  [ ] Accent uses theme.accentColor
  [ ] Icon tint uses theme.onSurface

Fix any hardcoded colors that should be theme-driven.
Pay special attention to: search TextField in StartMenu, bottom cluster buttons,
window control buttons (minimize/maximize/close), breadcrumbs in FileExplorer.

─── STEP 2: Missing polish items ────────────────────────────────────────────
Add:
1. Taskbar: when no windows are open, show a subtle "No open apps" text in
   the center or just the pinned apps row cleanly
2. StartMenu: when search returns no results, show "No results for '{query}'"
3. FileExplorer: when a directory cannot be read (SecurityException),
   show "Cannot access this folder" inline instead of crashing
4. All Snackbar / Toast messages should use the theme accent color
5. DesktopIconItem: icon labels should use Color.White with a subtle
   text shadow regardless of theme (desktop wallpaper is always dark)

─── STEP 3: Verify AndroidManifest completeness ──────────────────────────────
Read app/src/main/AndroidManifest.xml and confirm:
  [ ] CATEGORY_HOME intent-filter on MainActivity
  [ ] android:launchMode="singleTask"
  [ ] android:stateNotNeeded="true"
  [ ] MANAGE_EXTERNAL_STORAGE permission
  [ ] QUERY_ALL_PACKAGES permission
  [ ] PACKAGE_USAGE_STATS permission
  [ ] BootReceiver registered
If any are missing, add them.

─── STEP 4: Build debug APK ─────────────────────────────────────────────────
cd /data/workspace/ns11-build

# Make gradlew executable
chmod +x gradlew

# Download Gradle wrapper jar if not present
if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
  curl -Lo gradle/wrapper/gradle-wrapper.jar \
    https://github.com/gradle/gradle/raw/v8.11.1/gradle/wrapper/gradle-wrapper.jar 2>/dev/null || \
  # fallback: copy from another workspace repo
  cp /data/workspace/ForTheWin11/gradle/wrapper/gradle-wrapper.jar gradle/wrapper/ 2>/dev/null || \
  cp /data/workspace/NeverSoft-11/gradle/wrapper/gradle-wrapper.jar gradle/wrapper/ 2>/dev/null || true
fi

# Build debug APK
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
./gradlew assembleDebug --stacktrace 2>&1 | tail -60

─── STEP 5: Locate and publish APK ──────────────────────────────────────────
APK will be at: app/build/outputs/apk/debug/app-debug.apk

Verify it exists and check size:
  ls -lh app/build/outputs/apk/debug/

If build succeeded:
  publish_artifact("app/build/outputs/apk/debug/app-debug.apk")
  Print the download URL

─── STEP 6: If build fails — diagnose and fix ────────────────────────────────
If gradlew fails:
  1. Read the full error output
  2. Identify the first compile error (file + line)
  3. Read the offending file
  4. Fix the error
  5. Re-run ./gradlew assembleDebug
  6. Repeat until build passes (max 5 fix cycles)
  Common issues to watch for:
    - Unresolved references (missing imports)
    - Type mismatches in Compose parameters
    - Missing @Composable annotations
    - resource not found (missing drawable/mipmap stubs)

─── STEP 7: Final git commit + tag ──────────────────────────────────────────
git add .
git commit -m "Phase 7: Theme polish + APK v0.1.0-debug

- Full theme audit: all surfaces use LocalLauncherTheme.current
- Fixed hardcoded colors across StartMenu, FileExplorer, Taskbar
- Empty state messages: no results, no open apps, no access
- Debug APK built and published"
git tag v0.1.0-debug
git push origin main --tags

─── PHASE 7 COMPLETION REPORT ───────────────────────────────────────────────

══════════════════════════════════════════════════════════
  🏁 NEVERSOFT 11 v0.1.0 — ALL PHASES COMPLETE ✓
══════════════════════════════════════════════════════════
APK: [download URL here]
Repo: https://github.com/ether4o4/NeverSoft-11
Tag: v0.1.0-debug
Commit: [SHA]

Phases delivered:
  ✓ Phase 0 — Clean Kotlin Compose scaffold
  ✓ Phase 1 — Theme engine + DataStore persistence + onboarding
  ✓ Phase 2 — Window manager: drag, z-order, maximize/restore
  ✓ Phase 3 — Start menu + Spotlight internal module + pinning
  ✓ Phase 4 — File Explorer fully operational
  ✓ Phase 5 — Desktop DnD + Recycle Bin emblem
  ✓ Phase 6 — Task View animated cards
  ✓ Phase 7 — Theme polish + APK build

What's working in this APK:
  ✓ Windows 11 desktop shell — wallpaper, taskbar, desktop icons
  ✓ Start orb → Start menu with live Spotlight search
  ✓ Pin to Start / Pin to taskbar with persistence
  ✓ 4-row app grid (pinned first + usage-ranked)
  ✓ See All alphabetical app list
  ✓ Bottom cluster: Android Settings, Control Panel, File Explorer, Terminal (placeholder)
  ✓ Draggable popup windows with minimize/maximize/restore/close
  ✓ File Explorer: real folder navigation, 8 nav sections, file grid
  ✓ Control Panel: 10 theme presets, live switching
  ✓ Desktop drag/drop, edit mode, wiggle, remove shortcut
  ✓ Recycle Bin emblem on desktop
  ✓ Task View with animated window cards
  ✓ Full permissions onboarding on first launch

Next phase candidates:
  → Working Recycle Bin (move-to-bin, restore, empty)
  → Termux integration as a real terminal popup
  → Notification/clock flyout popup
  → Real window screenshot previews in Task View
  → App icon badge counts
══════════════════════════════════════════════════════════
```

---

## Quick Reference — Model Routing

| Phase | Work type | Model |
|---|---|---|
| 0 | Scaffold, Gradle, all Kotlin stubs | `gpt-5.3-codex` |
| 1 | Theme wiring, DataStore, Compose flows | `gpt-5.3-codex` |
| 2 | Drag gestures, z-order, animations | `gpt-5.3-codex` |
| **3** | **Spotlight multi-package merge, cross-file wiring** | **`claude-opus-4.8`** ← switch |
| 4 | File system queries, MediaStore, UI layout | `gpt-5.3-codex` |
| 5 | FTW DnD port, wiggle, gesture system | `gpt-5.3-codex` |
| 6 | Task View UI, animations | `gpt-5.3-codex` |
| **7** | **Cross-surface audit, APK build, debug loops** | **`claude-opus-4.8`** ← switch |
| Bug fix | Something subtle and architectural is wrong | `claude-opus-4.8` |
| Any time you need fast volume | Straightforward Kotlin/Compose work | `gpt-5.3-codex` |
