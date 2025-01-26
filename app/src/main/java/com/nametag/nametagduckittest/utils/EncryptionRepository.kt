package com.nametag.nametagduckittest.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class EncryptionRepository @Inject constructor(private val keyStore: KeyStore,
                                               private val keyGenerator: KeyGenerator,
                                               private val cipher: Cipher,
                                               private val dataStoreModule: DataStore<Preferences>) {

    private val keyAlias = "apiToken"

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