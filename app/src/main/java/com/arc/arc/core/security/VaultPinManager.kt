package com.arc.arc.core.security

import com.arc.arc.core.datastore.IVaultPreferencesDataStore
import java.security.GeneralSecurityException
import java.util.Base64

class VaultLockoutException(
    val remainingSeconds: Long
) : GeneralSecurityException(
    "Vault is temporarily locked due to repeated failed attempts. Try again in ${remainingSeconds}s."
)

class InvalidPinException(
    val attempts: Int,
    val lockoutSeconds: Long = 0L
) : GeneralSecurityException(
    if (lockoutSeconds > 0) "Incorrect PIN. Vault locked for ${lockoutSeconds}s."
    else "Incorrect PIN."
)

class VaultNotConfiguredException : GeneralSecurityException(
    "Vault has not been configured with a PIN."
)

/**
 * Manages Vault PIN creation, verification, rate limiting, and DEK derivation.
 *
 * CRITICAL SECURITY INVARIANTS:
 * 1. Low-entropy PINs are NEVER used as raw data encryption keys.
 * 2. KEK derivation utilizes PBKDF2 HMAC-SHA256 with 100,000 iterations and a 32-byte salt.
 * 3. PIN verification is zero-knowledge via AEAD tag validation during DEK unwrapping.
 * 4. Failed attempts trigger persistent exponential lockouts surviving process death.
 */
class VaultPinManager(
    private val preferencesDataStore: IVaultPreferencesDataStore
) {

    /**
     * Validates that a PIN consists only of digits and is between 3 and 6 digits.
     */
    fun validatePinFormat(pin: String, expectedLength: Int? = null): Result<Unit> {
        if (!pin.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("PIN must contain only numeric digits"))
        }
        if (pin.length !in 3..6) {
            return Result.failure(IllegalArgumentException("PIN length must be between 3 and 6 digits"))
        }
        if (expectedLength != null && pin.length != expectedLength) {
            return Result.failure(IllegalArgumentException("PIN must be exactly $expectedLength digits"))
        }
        return Result.success(Unit)
    }

    /**
     * Sets up an initial PIN for [ownerUid].
     * Derives a KEK, generates a cryptographically secure random 256-bit DEK,
     * wraps the DEK, and persists configuration.
     *
     * Returns the raw 32-byte DEK for the initial unlocked session.
     */
    suspend fun setupPin(
        ownerUid: String,
        pin: String,
        pinLength: Int = pin.length,
    ): Result<ByteArray> {
        val formatValidation = validatePinFormat(pin, pinLength)
        if (formatValidation.isFailure) {
            return Result.failure(formatValidation.exceptionOrNull()!!)
        }

        return try {
            val salt = VaultCryptoEngine.generateSalt(VaultCryptoEngine.SALT_LENGTH_BYTES)
            val kek = VaultCryptoEngine.deriveKeyFromPin(pin.toCharArray(), salt)
            val dek = VaultCryptoEngine.generateDataEncryptionKey()

            val dekBase64 = Base64.getEncoder().encodeToString(dek.encoded)
            val encryptedDek = VaultCryptoEngine.encryptToString(dekBase64, kek)
            val saltHex = VaultCryptoEngine.bytesToHex(salt)

            preferencesDataStore.saveVaultConfig(
                ownerUid = ownerUid,
                pinLength = pinLength,
                saltHex = saltHex,
                encryptedDek = encryptedDek,
            )

            Result.success(dek.encoded)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifies the user-entered PIN against persisted KEK-wrapped DEK.
     * Enforces persistent rate limiting and lockouts.
     *
     * On success: resets failed attempts and returns decrypted 32-byte DEK.
     * On failure: records failed attempt, applies lockout if threshold met, and throws [InvalidPinException].
     */
    suspend fun verifyPin(
        ownerUid: String,
        pin: String,
        currentTimeMs: Long = System.currentTimeMillis(),
    ): Result<ByteArray> {
        val config = preferencesDataStore.getVaultConfig(ownerUid)
        if (!config.isConfigured) {
            return Result.failure(VaultNotConfiguredException())
        }

        if (config.isLockedOut(currentTimeMs)) {
            val remainingSec = config.remainingLockoutSeconds(currentTimeMs)
            return Result.failure(VaultLockoutException(remainingSec))
        }

        return try {
            val salt = VaultCryptoEngine.hexToBytes(config.saltHex!!)
            val kek = VaultCryptoEngine.deriveKeyFromPin(pin.toCharArray(), salt)
            val decryptedDekBase64 = VaultCryptoEngine.decryptFromString(config.encryptedDek!!, kek)
            val dekBytes = Base64.getDecoder().decode(decryptedDekBase64)

            preferencesDataStore.resetFailedAttempts(ownerUid)
            Result.success(dekBytes)
        } catch (e: GeneralSecurityException) {
            val updatedConfig = preferencesDataStore.recordFailedAttempt(ownerUid, currentTimeMs)
            val lockoutSec = updatedConfig.remainingLockoutSeconds(currentTimeMs)
            Result.failure(InvalidPinException(updatedConfig.failedAttempts, lockoutSec))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Changes an existing PIN. Unwraps the DEK using [currentPin],
     * derives a new KEK using [newPin] and a fresh salt, and re-wraps the identical DEK.
     */
    suspend fun changePin(
        ownerUid: String,
        currentPin: String,
        newPin: String,
        newPinLength: Int = newPin.length,
        currentTimeMs: Long = System.currentTimeMillis(),
    ): Result<ByteArray> {
        val newFormatCheck = validatePinFormat(newPin, newPinLength)
        if (newFormatCheck.isFailure) {
            return Result.failure(newFormatCheck.exceptionOrNull()!!)
        }

        val verifyResult = verifyPin(ownerUid, currentPin, currentTimeMs)
        if (verifyResult.isFailure) {
            return verifyResult
        }
        val dek = verifyResult.getOrThrow()

        return try {
            val newSalt = VaultCryptoEngine.generateSalt(VaultCryptoEngine.SALT_LENGTH_BYTES)
            val newKek = VaultCryptoEngine.deriveKeyFromPin(newPin.toCharArray(), newSalt)
            val dekBase64 = Base64.getEncoder().encodeToString(dek)
            val newEncryptedDek = VaultCryptoEngine.encryptToString(dekBase64, newKek)
            val newSaltHex = VaultCryptoEngine.bytesToHex(newSalt)

            preferencesDataStore.saveVaultConfig(
                ownerUid = ownerUid,
                pinLength = newPinLength,
                saltHex = newSaltHex,
                encryptedDek = newEncryptedDek,
            )

            Result.success(dek)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Wipes vault configuration for [ownerUid].
     */
    suspend fun wipeVault(ownerUid: String): Result<Unit> {
        return try {
            preferencesDataStore.clearVaultConfig(ownerUid)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
