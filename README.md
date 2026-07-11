# NeverSoft 11

A 1:1 Windows 11 OS shell replica for Android — the Windows 11 desktop, taskbar, Start menu, File Explorer, Settings, and lock screen, rebuilt with Fluent design tokens and branded NeverSoft.

## What's in the shell (v2.0)
- **Bloom wallpaper** — procedural recreation of the Windows 11 default wallpaper, dark and light variants
- **Taskbar** — centered Start / Search / Task View / File Explorer, running-app indicators, system tray (network, volume, battery), two-line clock, show-desktop sliver
- **Start menu** — search box, Pinned app grid, All apps A–Z list, Recommended recent files, user + power footer (Lock, Sleep, Restart, Sign out)
- **Quick settings** — Wi-Fi / Bluetooth / Airplane / Battery saver / Night light / Accessibility, working brightness and volume sliders
- **Notification center + calendar** flyout on the clock
- **Windows** — Mica chrome, drag, resize grip, minimize / maximize / close caption buttons, Task View
- **File Explorer** — command bar (New, cut, copy, paste, rename, delete), breadcrumb address bar, sidebar, details columns, status bar, and a working **Recycle Bin**
- **Settings app** — Win11 Settings layout with Personalization (Light/Dark mode), System, and Apps pages
- **Lock screen** and Win11 OOBE-style first-run setup

## Build
```bash
./gradlew assembleDebug
```

## Architecture
See `neversoft-11-mashup-spec.md` for full spec.

## Phases
| Phase | Status | Description |
|---|---|---|
| 0 | ✅ | Clean Kotlin Compose scaffold |
| 1 | ✅ | Theme engine + DataStore |
| 2 | ✅ | Window manager |
| 3 | ✅ | Start menu + Spotlight |
| 4 | ✅ | File Explorer |
| 5 | ✅ | Desktop DnD |
| 6 | ✅ | Task View |
| 7 | ✅ | Polish + APK |

## 📥 Download

**[⬇️ Download the latest APK](https://github.com/ether4o4/NeverSoft-11/releases/download/latest/NeverSoft11.apk)**

This link always serves the newest release build — CI rebuilds and republishes it on every push to `main`. Install it, press Home, and pick **NeverSoft 11** as your launcher. If Android blocks the install, allow "install unknown apps" for your browser or file manager.
