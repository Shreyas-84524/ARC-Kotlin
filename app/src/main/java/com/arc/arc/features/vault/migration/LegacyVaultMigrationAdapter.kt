package com.arc.arc.features.vault.migration

import com.arc.arc.core.model.SecretNoteModel
import com.arc.arc.features.vault.data.ISecretNoteRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class LegacyMigrationResult(
    val migratedCount: Int,
    val skippedCount: Int,
    val errors: List<String> = emptyList(),
)

/**
 * Migration adapter for upgrading legacy unencrypted Flutter Hive notes (`secret_notes_data`)
 * into ARC's AES-256-GCM encrypted native Room vault.
 *
 * CRITICAL SECURITY INVARIANT:
 * Plaintext legacy notes are transformed directly into AES-256-GCM versioned envelopes
 * before persisting to Room SQLite storage. Legacy unencrypted storage must be cleared post-migration.
 */
class LegacyVaultMigrationAdapter(
    private val repository: ISecretNoteRepository
) {
    private val gson = Gson()

    /**
     * Migrates a list of raw legacy Flutter note maps into the encrypted vault for [ownerUid] using [dek].
     */
    suspend fun migrateLegacyMaps(
        ownerUid: String,
        legacyMaps: List<Map<*, *>>,
        dek: ByteArray
    ): LegacyMigrationResult {
        require(dek.size == 32) { "A valid 256-bit DEK is required for encrypted migration" }

        var migrated = 0
        var skipped = 0
        val errors = mutableListOf<String>()

        legacyMaps.forEach { rawMap ->
            try {
                val model = SecretNoteModel.fromMap(rawMap).copy(ownerUid = ownerUid)
                if (model.isValid) {
                    val saveResult = repository.saveNote(model, dek)
                    if (saveResult.isSuccess) {
                        migrated++
                    } else {
                        skipped++
                        errors.add("Failed to save note ${model.id}: ${saveResult.exceptionOrNull()?.message}")
                    }
                } else {
                    skipped++
                    errors.add("Skipped invalid empty legacy note")
                }
            } catch (e: Exception) {
                skipped++
                errors.add("Failed to parse legacy note: ${e.message}")
            }
        }

        return LegacyMigrationResult(
            migratedCount = migrated,
            skippedCount = skipped,
            errors = errors
        )
    }

    /**
     * Migrates raw legacy JSON string (from Hive backup or export) into the encrypted vault.
     */
    suspend fun migrateLegacyJson(
        ownerUid: String,
        legacyJsonString: String,
        dek: ByteArray
    ): LegacyMigrationResult {
        require(dek.size == 32) { "A valid 256-bit DEK is required for encrypted migration" }

        return try {
            val listType = object : TypeToken<List<Map<String, Any?>>>() {}.type
            val list: List<Map<String, Any?>> = gson.fromJson(legacyJsonString, listType)
            migrateLegacyMaps(ownerUid, list, dek)
        } catch (e: Exception) {
            LegacyMigrationResult(
                migratedCount = 0,
                skippedCount = 0,
                errors = listOf("Malformed legacy JSON export: ${e.message}")
            )
        }
    }
}
