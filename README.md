# NeverSoft 11


## Latest polish test APK

[Download the newest verified test APK — rolling release page](https://github.com/ether4o4/NeverSoft-11/releases/tag/polish-test-latest)

This separate prerelease channel contains **test APKs**, with source commit, package/version, native ABI and SHA-256 recorded on the release page. Open **Download newest verified test APK** on that page. Phone runtime testing is incomplete; test signing may differ from an installed/store version. Private repository downloads require GitHub access.

While this PR remains unmerged, a successful **Polish verification** build on `polish/mobile-2026-10-05` refreshes the channel (remove/reapply the `polish-verify` PR label to run verification). The initial download reuses the already verified polish build. After merge, successful **Android CI** builds on `main` also refresh it through `workflow_run`. Only trusted same-repository intended workflows/refs qualify; failed, older or diverging builds leave the working download intact. Versioned APK assets remain available, avoiding a replacement gap. Existing release channels keep their current behavior.

The stable link opens a release page; the highlighted APK filename changes after each accepted build. The channel tag anchors `main`; the release body identifies the actual APK source commit. These README additions and future `main` automation take effect on the default branch only after this PR is merged.


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
