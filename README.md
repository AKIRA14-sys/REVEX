# REVEX - Game Lag Booster, Launcher, Device Monitor & Crosshair Overlay App

**REVEX** is a complete, offline native Android gaming optimization suite built with **Kotlin** and **Jetpack Compose** (Min SDK 26, Target SDK 34). Package name: `com.akiratech.revex`.

---

## Key Features

1. **Custom Canvas Animated Sharingan Play Button**
   - Canvas-rendered 3-Tomoe & Mangekyō Sharingan eye design with crimson & gold accents.
   - Smooth idle spinning animation and ultra-fast spin transition during active boost execution.

2. **Forced Landscape UI & Horizontal Pager Navigation**
   - Locked to `sensorLandscape` orientation for seamless mobile gaming landscape experience.
   - Smooth 5-page `HorizontalPager` layout:
     - **HOME**: Live latency telemetry (Ping/Jitter), RAM monitor, battery thermal sensor, interactive Sharingan play button.
     - **GAMES**: Game library scanner (`QUERY_ALL_PACKAGES`, `CATEGORY_GAME` filter, custom game package additions, auto-boost launch).
     - **BOOST**: Memory cleaner (`killBackgroundProcesses`), API 31+ `GameManager` performance mode, Do Not Disturb policy toggle, Sustained Performance Mode, Thermal Guard alert shield.
     - **CROSSHAIRS**: Parametric vector crosshair studio with 500+ presets across 7 tiers (Sharingan, Red Dot, Regular, Pro, Premium, Legendary, God).
     - **DEVICE**: Realtime hardware diagnostics (RAM breakdown, battery health/temp, internal storage, game booster usage lifetime stats).

3. **500+ Parametric Vector Crosshair Engine**
   - Vector-rendered crosshairs with customizable size, thickness, gap, opacity, and rotation.
   - **Sharingan Crosshair Rules**:
     - Transparent/empty inner center ring.
     - Selectable center mark (Dot, Cross, None) with independent size and color controls.
     - Separate Sharingan opacity slider (10% to 100%).

4. **Floating Overlay HUD & System Service**
   - Floating bubble widget powered by `SYSTEM_ALERT_WINDOW` and foreground service (`FOREGROUND_SERVICE_SPECIAL_USE`).
   - Draggable on-screen bubble that expands into an interactive mini gaming control panel.

---

## Project Structure

```
REVEX/
├── .github/workflows/build.yml
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/akiratech/revex/
│           │   ├── RevexApplication.kt
│           │   ├── booster/
│           │   │   └── LagBoosterManager.kt
│           │   ├── data/
│           │   │   ├── crosshair/CrosshairGenerator.kt
│           │   │   ├── model/ (GameInfo, CrosshairConfig, DeviceStats, BoostConfig)
│           │   │   └── pref/RevexPreferences.kt
│           │   ├── monitor/
│           │   │   └── DeviceMonitorManager.kt
│           │   ├── service/
│           │   │   └── OverlayService.kt
│           │   └── ui/
│           │       ├── MainActivity.kt
│           │       ├── components/ (CrosshairCanvas, SharinganPlayButton)
│           │       ├── screens/ (HomeScreen, GamesScreen, BoostScreen, CrosshairsScreen, DeviceScreen)
│           │       └── theme/
│           └── res/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

---

## Building the Project

### Command Line
Run the debug build using the Gradle wrapper:
```bash
./gradlew assembleDebug
```
The generated APK will be produced at:
`app/build/outputs/apk/debug/app-debug.apk`

### Running Unit Tests
```bash
./gradlew testDebugUnitTest
```

---

## License & Credits
Built for REVEX Mobile Gaming Systems. Offline, privacy-first, 0 backend dependencies, 0 ads, 0 tracking.
