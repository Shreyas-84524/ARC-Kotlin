# ARC Kotlin

Native Android successor to the ARC Flutter productivity application. This repository contains the native Kotlin architecture, Jetpack Compose design system, Room persistence foundation, development authentication, authenticated navigation shell, and the complete offline-first Tasks vertical slice established in **Phase 1**.

## Objectives

- Preserve the production identity and user-data contracts of the original ARC application (`C:\dev\ARC`).
- Replace Flutter/Dart incrementally with Kotlin, Jetpack Compose, Room SQLite persistence, and Android-native integrations.
- Guarantee offline-first reliability, strict user data isolation, and smooth Compose performance.

## Stack & Toolchain

- **Build System:** Android Gradle Plugin 9.4.1 (with AGP built-in Kotlin support) & Gradle 9.8.1
- **Language & Runtime:** Kotlin 2.4.21, Java 17 bytecode
- **UI Framework:** Jetpack Compose (BOM 2026.09.00), Material 3
- **Local Persistence:** AndroidX Room 2.6.1 (SQLite) with schema export, DataStore Preferences 1.1.2
- **Concurrency & Architecture:** Kotlin Coroutines 1.10.1, StateFlow, Unidirectional Data Flow (UDF), ViewModel
- **SDK Targets:** Compile SDK 37, Target SDK 36, Min SDK 23

The production namespace remains `com.arc.arc`. Debug builds use `com.arc.arc.native.dev`, preventing development installs from colliding with the Flutter app or touching production data.

## Build & Test Instructions

1. Ensure JDK 17 (or Android Studio bundled runtime) and Android SDK 37 are configured in `local.properties`.
2. Build debug APK:
   ```shell
   .\gradlew.bat :app:assembleDebug
   ```
3. Run automated unit tests:
   ```shell
   .\gradlew.bat :app:testDebugUnitTest
   ```
4. Run Android Lint:
   ```shell
   .\gradlew.bat :app:lintDebug
   ```

## Repository Structure

```
app/src/main/java/com/arc/arc/
├── core/
│   ├── auth/              # Auth interfaces, session state, DevFakeAuthRepository
│   ├── database/          # Room database (v1), TaskDao, TaskEntity
│   ├── datastore/         # DataStore preferences for theme and settings
│   ├── designsystem/      # Compose design system tokens and reusable components
│   ├── di/                # AppContainer dependency injection
│   ├── migration/         # Hive to Room migration contract
│   └── model/             # Domain models (Task, Habit, Transaction, UserProfile)
├── features/
│   ├── auth/              # Login screen and LoginViewModel
│   ├── dashboard/         # Dashboard placeholder (Phase 2 target)
│   ├── habits/            # Habits placeholder (Phase 2 target)
│   ├── money/             # Money placeholder (Phase 3 target)
│   ├── navigation/        # ArcNavHost and 4-tab MainShellScreen
│   └── tasks/             # Offline-first Tasks module (UI, ViewModel, Repository)
├── ArcApplication.kt      # Application subclass
└── MainActivity.kt        # Entry point ComponentActivity
```

## Documentation

- **Phase 0 Baseline:** [docs/phase-0/PHASE_0_REPORT.md](docs/phase-0/PHASE_0_REPORT.md)
- **Phase 1 Documentation Suite:**
  - Executive Report: [docs/phase-1/PHASE_1_REPORT.md](docs/phase-1/PHASE_1_REPORT.md)
  - Architecture: [docs/phase-1/ARCHITECTURE_IMPLEMENTATION.md](docs/phase-1/ARCHITECTURE_IMPLEMENTATION.md)
  - Data Contracts: [docs/phase-1/DATA_CONTRACT_RESULTS.md](docs/phase-1/DATA_CONTRACT_RESULTS.md)
  - Tasks Implementation: [docs/phase-1/TASKS_IMPLEMENTATION.md](docs/phase-1/TASKS_IMPLEMENTATION.md)
  - Test Results: [docs/phase-1/TEST_RESULTS.md](docs/phase-1/TEST_RESULTS.md)
  - Performance Results: [docs/phase-1/PERFORMANCE_RESULTS.md](docs/phase-1/PERFORMANCE_RESULTS.md)
  - Remaining Integrations: [docs/phase-1/REMAINING_INTEGRATIONS.md](docs/phase-1/REMAINING_INTEGRATIONS.md)
  - Phase 2 Handoff: [docs/phase-1/PHASE_2_HANDOFF.md](docs/phase-1/PHASE_2_HANDOFF.md)
