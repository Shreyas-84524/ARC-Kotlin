package com.arc.arc.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Android Keystore-backed implementation of [IVaultMasterKeyProvider].
 *
 * Keys generated here reside in the device hardware Trusted Execution Environment (TEE)
 * or StrongBox Keymaster, preventing key material exfiltration even if root or physical storage
 * access is achieved.
 */
class AndroidKeystoreMasterKeyProvider(
    private val keyStoreType: String = "AndroidKeyStore"
) : IVaultMasterKeyProvider {

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(keyStoreType).apply { load(null) }
    }

    override val isHardwareBacked: Boolean
        get() = true

    @Synchronized
    override fun getOrCreateMasterKey(alias: String): SecretKey {
        if (keyStore.containsAlias(alias)) {
            val entry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
            if (entry != null) return entry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            keyStoreType
        )

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    @Synchronized
    override fun deleteMasterKey(alias: String) {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }
}
