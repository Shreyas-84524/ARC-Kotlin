# ARC Kotlin

Native Android successor to the existing ARC Flutter productivity application. This repository currently contains the verified Phase 0 foundation only; application features have deliberately not been ported yet.

## Objectives

- Preserve the production identity and user-data contracts of `C:\dev\ARC`.
- Replace Flutter/Dart incrementally with Kotlin, Jetpack Compose, and Android-native integrations.
- Improve measurable startup, package size, memory use, reliability, and maintainability without claiming gains before comparable release benchmarks exist.

## Stack

Kotlin with AGP built-in Kotlin support, Jetpack Compose + Material 3, Gradle Kotlin DSL, Gradle version catalog, AndroidX, JDK 17 bytecode, compile SDK 37, target SDK 36, and minimum SDK 23.

The production namespace remains `com.arc.arc`. Debug builds use `com.arc.arc.native.dev`, preventing Phase 0 installs from replacing the Flutter app or touching its sandbox.

## Build on Windows

1. Install Android Studio and SDK Platform 37 / Build Tools 36.0.0.
2. Use Android Studio's bundled runtime or a compatible JDK (minimum 17).
3. Create untracked `local.properties` with `sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk`, or let Android Studio generate it.
4. Run `./gradlew.bat :app:assembleDebug`.
5. Run `./gradlew.bat :app:testDebugUnitTest :app:lintDebug`.

No Firebase configuration is required for the Phase 0 placeholder.

## Structure

- `app/`: minimal Compose application.
- `gradle/libs.versions.toml`: centralized build and dependency versions.
- `docs/phase-0/`: Flutter audit, migration design, security analysis, and handoff.

Feature modules will be introduced only when Phase 1 boundaries are demonstrated. The intended layers are presentation, domain, data, and core services with feature-oriented packaging.

## Status and documentation

Phase 0 is documented in [PHASE_0_REPORT.md](docs/phase-0/PHASE_0_REPORT.md). Start with [ANTIGRAVITY_HANDOFF.md](docs/phase-0/ANTIGRAVITY_HANDOFF.md) for Phase 1 and consult [USER_ACTIONS_REQUIRED.md](docs/phase-0/USER_ACTIONS_REQUIRED.md) before enabling Firebase, signing, SMS access, or production identity installation.
