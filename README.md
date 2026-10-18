# ARC Kotlin

Native Android successor to the ARC Flutter productivity application. This repository contains the native Kotlin architecture, Jetpack Compose design system, Room persistence foundation, development authentication, authenticated navigation shell, the offline-first Tasks vertical slice established in **Phase 1**, the native Productivity Dashboard, Habit Tracker, and Room Schema v2 established in **Phase 2**, the native Money Manager, Financial Intelligence Engine, Automatic Expense Detection, and Room Schema v3 established in **Phase 3**, and the native Encrypted Secret Vault, AES-256-GCM Cryptographic Engine, PBKDF2 Key Derivation, Biometric Single-Prompt Security, and Room Schema v4 established in **Phase 4**.

## Objectives

- Preserve the production identity and user-data contracts of the original ARC application (`C:\dev\ARC`).
- Replace Flutter/Dart incrementally with Kotlin, Jetpack Compose, Room SQLite persistence, and Android-native integrations.
- Guarantee offline-first reliability, strict user data isolation, non-floating-point financial accuracy, and smooth Compose performance.

## Stack & Toolchain

- **Build System:** Android Gradle Plugin 9.4.1 (with AGP built-in Kotlin support) & Gradle 9.8.1
- **Language & Runtime:** Kotlin 2.4.21, Java 17 bytecode, Java 8+ API desugaring (`desugar_jdk_libs:2.1.4`)
- **UI Framework:** Jetpack Compose (BOM 2026.09.00), Material 3
- **Local Persistence:** AndroidX Room 2.6.1 (SQLite v3) with schema export, DataStore Preferences 1.1.2
- **Concurrency & Architecture:** Kotlin Coroutines 1.10.1, StateFlow, Unidirectional Data Flow (UDF), ViewModel
- **SDK Targets:** Compile SDK 37, Target SDK 36, Min SDK 23

The production namespace remains `com.arc.arc`. Debug builds use `com.arc.arc.native.dev`, preventing development installs from colliding with the Flutter app or touching production data.

## Build & Test Instructions

1. Ensure JDK 17 (or Android Studio bundled runtime) and Android SDK 37 are configured in `local.properties`.
2. Build debug APK:
   ```shell
   .\gradlew.bat :app:assembleDebug
   ```
3. Run automated unit tests (160 tests across 30 suites):
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
│   ├── database/          # Room database (v4), Task, Habit, HabitEntry, Transaction, Budget, SecretNote DAOs & Entities
│   ├── datastore/         # DataStore preferences for theme, settings, financial privacy, and vault rate-limiting
│   ├── designsystem/      # Compose design system tokens and reusable components
│   ├── di/                # AppContainer dependency injection (including Vault crypto and managers)
│   ├── migration/         # Hive to Room migration contract & legacy notes migration
│   ├── model/             # Domain models (Task, Habit, HabitEntry, Transaction, Budget, SecretNote, UserProfile)
│   └── utilities/         # CurrencyFormatter (paise-backed INR formatting)
├── features/
│   ├── auth/              # Login screen and LoginViewModel
│   ├── dashboard/         # Reactive Productivity Dashboard (Score engine, UI, ViewModel, cross-module cards)
│   ├── habits/            # Offline-first Habit Tracker (Streak engine, Card, Editor, ViewModel)
│   ├── money/             # Offline-first Money Manager (UI, ViewModel, Repository, Analytics Engine, Automation)
│   ├── navigation/        # ArcNavHost and 5-tab MainShellScreen (Tasks, Habits, Dashboard, Money, Vault)
│   ├── tasks/             # Offline-first Tasks module (UI, ViewModel, Repository)
│   └── vault/             # Local encrypted vault (Crypto engine, Biometrics, PIN keypad, Editor, ViewModel)
├── ArcApplication.kt      # Application subclass
└── MainActivity.kt        # Entry point FragmentActivity with dynamic FLAG_SECURE & auto-lock
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
- **Phase 2 Documentation Suite:**
  - Executive Report: [docs/phase-2/PHASE_2_REPORT.md](docs/phase-2/PHASE_2_REPORT.md)
  - Habits Implementation: [docs/phase-2/HABITS_IMPLEMENTATION.md](docs/phase-2/HABITS_IMPLEMENTATION.md)
  - Dashboard Implementation: [docs/phase-2/DASHBOARD_IMPLEMENTATION.md](docs/phase-2/DASHBOARD_IMPLEMENTATION.md)
  - Database Migration Results: [docs/phase-2/DATABASE_MIGRATION_RESULTS.md](docs/phase-2/DATABASE_MIGRATION_RESULTS.md)
  - Offline Integration Results: [docs/phase-2/OFFLINE_INTEGRATION_RESULTS.md](docs/phase-2/OFFLINE_INTEGRATION_RESULTS.md)
  - Test Results: [docs/phase-2/TEST_RESULTS.md](docs/phase-2/TEST_RESULTS.md)
  - Performance Results: [docs/phase-2/PERFORMANCE_RESULTS.md](docs/phase-2/PERFORMANCE_RESULTS.md)
  - Remaining Integrations: [docs/phase-2/REMAINING_INTEGRATIONS.md](docs/phase-2/REMAINING_INTEGRATIONS.md)
  - Phase 3 Handoff: [docs/phase-2/PHASE_3_HANDOFF.md](docs/phase-2/PHASE_3_HANDOFF.md)
- **Phase 3 Documentation Suite:**
  - Executive Report: [docs/phase-3/PHASE_3_REPORT.md](docs/phase-3/PHASE_3_REPORT.md)
  - Money Implementation: [docs/phase-3/MONEY_IMPLEMENTATION.md](docs/phase-3/MONEY_IMPLEMENTATION.md)
  - Financial Intelligence Engine: [docs/phase-3/FINANCIAL_INTELLIGENCE.md](docs/phase-3/FINANCIAL_INTELLIGENCE.md)
  - Expense Detection Engine: [docs/phase-3/EXPENSE_DETECTION_ENGINE.md](docs/phase-3/EXPENSE_DETECTION_ENGINE.md)
  - SMS Policy & Compliance: [docs/phase-3/SMS_POLICY_AND_COMPLIANCE.md](docs/phase-3/SMS_POLICY_AND_COMPLIANCE.md)
  - Cross-Module Integration: [docs/phase-3/CROSS_MODULE_INTEGRATION.md](docs/phase-3/CROSS_MODULE_INTEGRATION.md)
  - Database Migration V3: [docs/phase-3/DATABASE_MIGRATION_V3.md](docs/phase-3/DATABASE_MIGRATION_V3.md)
  - Data Contract Results: [docs/phase-3/DATA_CONTRACT_RESULTS.md](docs/phase-3/DATA_CONTRACT_RESULTS.md)
  - Test Results: [docs/phase-3/TEST_RESULTS.md](docs/phase-3/TEST_RESULTS.md)
  - Performance Results: [docs/phase-3/PERFORMANCE_RESULTS.md](docs/phase-3/PERFORMANCE_RESULTS.md)
  - Security Audit: [docs/phase-3/SECURITY_AUDIT.md](docs/phase-3/SECURITY_AUDIT.md)
  - Remaining Integrations: [docs/phase-3/REMAINING_INTEGRATIONS.md](docs/phase-3/REMAINING_INTEGRATIONS.md)
  - Phase 4 Handoff: [docs/phase-3/PHASE_4_HANDOFF.md](docs/phase-3/PHASE_4_HANDOFF.md)
- **Phase 4 Documentation Suite:**
  - Executive Report: [docs/phase-4/PHASE_4_REPORT.md](docs/phase-4/PHASE_4_REPORT.md)
  - Cryptography Architecture: [docs/phase-4/CRYPTOGRAPHY_ARCHITECTURE.md](docs/phase-4/CRYPTOGRAPHY_ARCHITECTURE.md)
  - Key Management: [docs/phase-4/KEY_MANAGEMENT.md](docs/phase-4/KEY_MANAGEMENT.md)
  - PIN & Biometric Authentication: [docs/phase-4/PIN_AND_BIOMETRIC_AUTH.md](docs/phase-4/PIN_AND_BIOMETRIC_AUTH.md)
  - Vault Implementation: [docs/phase-4/VAULT_IMPLEMENTATION.md](docs/phase-4/VAULT_IMPLEMENTATION.md)
  - Auto-Lock & Privacy: [docs/phase-4/AUTO_LOCK_AND_PRIVACY.md](docs/phase-4/AUTO_LOCK_AND_PRIVACY.md)
  - Legacy Notes Migration: [docs/phase-4/LEGACY_NOTES_MIGRATION.md](docs/phase-4/LEGACY_NOTES_MIGRATION.md)
  - Security Test Results: [docs/phase-4/SECURITY_TEST_RESULTS.md](docs/phase-4/SECURITY_TEST_RESULTS.md)
  - Performance Results: [docs/phase-4/PERFORMANCE_RESULTS.md](docs/phase-4/PERFORMANCE_RESULTS.md)
  - Remaining Integrations: [docs/phase-4/REMAINING_INTEGRATIONS.md](docs/phase-4/REMAINING_INTEGRATIONS.md)
  - Phase 5 Handoff: [docs/phase-4/PHASE_5_HANDOFF.md](docs/phase-4/PHASE_5_HANDOFF.md)


