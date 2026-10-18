# ARC Kotlin agent instructions

1. Read every file under `docs/phase-0/` before feature work, beginning with `ANTIGRAVITY_HANDOFF.md`.
2. Treat `C:\dev\ARC` as read-only. Never edit, format, generate into, clean, reset, or restructure the Flutter repository without explicit user permission.
3. Implement new Android work in Kotlin and Jetpack Compose and follow the architecture and sequence in `KOTLIN_ARCHITECTURE.md` and `MIGRATION_ROADMAP.md`.
4. Preserve application identity, Firebase authentication identities, Firestore paths, local record identifiers, timestamp semantics, and user data. Do not run a real-data migration without an approved, tested rollback plan.
5. Never hardcode or commit credentials, service-account files, signing keys, `.env` values, private certificates, or unreviewed `google-services.json` files.
6. Prefer stable, mutually compatible dependencies in the version catalog. Record intentional version or architecture changes.
7. Follow Kotlin style, keep composables small and stateless where practical, expose immutable UI state, and use coroutines/Flow for asynchronous boundaries.
8. Run focused tests plus `:app:testDebugUnitTest`, `:app:lintDebug`, and the relevant assemble task after modifications.
9. Keep Phase 0 and later migration documentation current. Do not introduce undocumented breaking changes.
10. Use conventional commits. Inspect staged changes and scan for secrets before committing.
11. Never force-push, rewrite history, discard user changes, or perform destructive Git operations without explicit authorization.
