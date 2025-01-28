package com.nametag.nametagduckittest.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

/**
 * Repository for encryption and decryption of the token from the data store.
 * @param keyStore The key store provided by Hilt
 * @param keyGenerator The key generator provided by Hilt
 * @param cipher The cipher provided by Hilt
 * @param dataStoreModule The data store provided by Hilt
 */
class EncryptionRepository @Inject constructor(private val keyStore: KeyStore,
                                               private val keyGenerator: KeyGenerator,
                                               private val cipher: Cipher,
                                               private val dataStoreModule: DataStore<Preferences>) {

    //Key alias for the key store, hardcoded for now as the app only is encrypted with one key
    private val keyAlias = "apiToken"

    /**
     * Generates a secret key for the key store.
     * @return The secret key
     */
    private fun generateSecretKey(): SecretKey {
        val keyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry

        return keyEntry?.secretKey ?: run {
            keyGenerator.apply {
                init(
                    KeyGenParameterSpec
                        .Builder(keyAlias, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE_GCM)
                        .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
        }
    }

    /**
     * Encrypts the data and stores it in the data store.
     * @param data The data to encrypt, at the moment is only the api token
     */
    suspend fun encryptData(data: String) {
        val secretKey = generateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray())
        dataStoreModule.edit { preferences ->
            preferences[stringPreferencesKey("iv")] = Base64.encodeToString(iv, Base64.DEFAULT)
            preferences[stringPreferencesKey("encryptedData")] = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        }
    }

    /**
     * Decrypts the data from the data store.
     * @return The decrypted token, or null if there is no token in the data store
     */
    suspend fun decryptData(): String? {
        val preferences = dataStoreModule.data.first()
        val iv = preferences[stringPreferencesKey("iv")] ?: return null
        val encryptedData = preferences[stringPreferencesKey("encryptedData")] ?: return null
        val secretKey = generateSecretKey()
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(128, Base64.decode(iv, Base64.DEFAULT))
        )

        return String(cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT)))
    }
}