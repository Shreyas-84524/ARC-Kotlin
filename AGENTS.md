# ARC Kotlin Agent Instructions

1. **Review Phase Handoffs:** Before starting work, review `docs/phase-0/`, `docs/phase-1/`, `docs/phase-2/`, `docs/phase-3/`, `docs/phase-4/`, and all files under `docs/phase-5/`, particularly `docs/phase-5/PHASE_6_HANDOFF.md`, `docs/phase-5/PHASE_5_REPORT.md`, `docs/phase-5/SYSTEM_INTEGRATION.md`, and `docs/phase-5/TEST_RESULTS.md`.
2. **Read-Only Legacy Reference:** Treat `C:\dev\ARC` as strictly read-only. Never edit, format, generate into, clean, reset, or restructure the Flutter repository without explicit user permission.
3. **Architecture & Persistence Standards:**
   - Follow the established native architecture in `com.arc.arc`.
   - Domain models reside in `core/model/`.
   - Room database entities and DAOs reside in `core/database/` (authored in Java to interop with AGP built-in Kotlin annotation processing) with Kotlin extension mappers in `core/model/`.
   - Reusable UI elements must use `ArcTheme` tokens in `core/designsystem/`.
   - ViewModels must implement a companion `ViewModelProvider.Factory` and be retrieved via `viewModel(factory = ...)`.
4. **Data Contract Integrity:** Preserve verified record schemas, timestamp formatting (ISO-8601 UTC), enum mappings, composite key patterns (`${habitId}_${dateStr}` for HabitEntry), non-floating-point financial math (integer paise `Long`), and user scoping (`ownerUid` / `userId`).
5. **Security Safeguards:**
   - Never commit credentials, keystores, or production `google-services.json` files.
   - Development auth must maintain `isDevFake = true` and keep development records strictly isolated from production Firebase.
   - Never request or add restricted SMS permissions without approved Play Store policy exception.
   - **Secret Vault Integrity:** Secret notes are strictly local-only (never sync to Firebase). Encrypt using hardware-backed Android Keystore AES-256-GCM master key and PBKDF2 HMAC-SHA256 derived keys. Zero plaintext at rest. Dynamic `FLAG_SECURE` window protection must protect vault screens.
   - **Zero-Cloud Vault Search & Notification Isolation:** Secret Notes are strictly prohibited from global search indexing and notification reminder dispatch.
   - **Battery & Policy Safety:** Never declare `USE_EXACT_ALARM` or `SCHEDULE_EXACT_ALARM`; use inexact alarms with graceful fallback.
6. **Toolchain Discipline:** Do not re-introduce legacy plugins (such as `id("kotlin-kapt")` or external `kotlin-android`) incompatible with AGP 9.4.1 built-in Kotlin support.
7. **Quality Gates:** Every phase or major feature must verify clean compilation, 100% test pass rate (`:app:testDebugUnitTest`), zero Android Lint errors (`:app:lintDebug`), and successful packaging (`:app:assembleDebug`).
8. **Git Best Practices:** Use conventional commits. Never force-push, rewrite history, or discard existing work.

