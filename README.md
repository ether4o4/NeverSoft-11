# NeverSoft 11

Windows 11–style Android launcher (`com.neversoft.launcher`).

The original repository was accidentally deleted. This repo holds everything
recovered from the last surviving build, **NeverSoft11-v0.1.1.apk**, plus the
recovery notes below.

## What's in here

| Path | Contents |
|---|---|
| `recovery/NeverSoft11-v0.1.1.apk` | The last surviving build (installable) |
| `recovery/decompiled-src/` | App source decompiled from the APK with jadx (Java form of the original Kotlin) |
| `recovery/resources/AndroidManifest.xml` | Decoded manifest |
| `recovery/resources/res/` | Decoded resources (themes, strings, drawables, icons) |
| `recovery/resources/assets/` | App assets |

## Build info recovered from the APK

- App name: **NeverSoft 11**, package `com.neversoft.launcher`
- versionName `11.0`, versionCode 1, minSdk 29, targetSdk 35
- Jetpack Compose (Material 3), Kotlin 2.0.21, Gradle 8.11.1
- Built from git commit `c6d05581eac0961dd7905c0a945d24ba151912e2` (original repo, now deleted)
- APK SHA-256: `f916de19db45bd6eb140bd5cc10344c613cbde2bbf7c3b48d50597425277d731`

## Restoring the original repo (best option)

GitHub keeps deleted repositories restorable for ~90 days:
**Settings → Repositories → Deleted repositories** → <https://github.com/settings/deleted_repositories>.
Restoring it brings back the full Kotlin source and commit history — strictly
better than the decompiled code here. This repo is the fallback if that window
has passed.

## App structure (from the decompiled source)

- `MainActivity` — entry point, immersive shell
- `ui/Shell` — top-level layout: desktop, taskbar, overlays
- `ui/Desktop`, `ui/Taskbar`, `ui/StartMenu`, `ui/TaskView`, `ui/Widgets`, `ui/Flyouts`, `ui/ContextMenu` — the Windows-11-style surfaces
- `ui/window/AppWindow` — floating/snapped app windows
- `ui/modifier/Acrylic` — acrylic blur modifier
- `ui/theme/` — NsColor / NsDim / Theme
- `apps/AppRepository`, `apps/AppEntry` — installed-app catalog
