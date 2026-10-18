package com.arc.arc.core.security

import javax.crypto.SecretKey

/**
 * Contract for acquiring or generating hardware-backed master wrapping keys.
 *
 * CRITICAL DEFENSE-IN-DEPTH:
 * Shields small-PIN vaults against offline dictionary attacks. The master key is rooted
 * in hardware (TEE / StrongBox) and cannot be extracted from flash storage dumps.
 */
interface IVaultMasterKeyProvider {
    val isHardwareBacked: Boolean
    fun getOrCreateMasterKey(alias: String): SecretKey
    fun deleteMasterKey(alias: String)
}

/**
 * In-memory provider for unit tests and local JVM execution.
 */
class InMemoryMasterKeyProvider(
    override val isHardwareBacked: Boolean = false
) : IVaultMasterKeyProvider {
    private val keys = mutableMapOf<String, SecretKey>()

    override fun getOrCreateMasterKey(alias: String): SecretKey {
        return keys.getOrPut(alias) {
            VaultCryptoEngine.generateDataEncryptionKey()
        }
    }

    override fun deleteMasterKey(alias: String) {
        keys.remove(alias)
    }
}
