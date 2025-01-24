package com.nametag.nametagduckittest.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class EncryptionRepository @Inject constructor(private val keystoreModule: KeystoreModule, private val dataStoreModule: DataStore<Preferences>) {

    private fun generateSecretKey(keyAlias: String): SecretKey {
        val keyEntry = keystoreModule.provideKeyStore().getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry

        return keyEntry?.secretKey ?: run {
            keystoreModule.provideKeyGenerator().apply {
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

    suspend fun encryptData(data: String, keyAlias: String) {
        val secretKey = generateSecretKey(keyAlias)
        val cipher = keystoreModule.provideCipher()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray())
        dataStoreModule.edit { preferences ->
            preferences[stringPreferencesKey("iv")] = iv.toString()
            preferences[stringPreferencesKey("encryptedData")] = encryptedData.toString()
        }
    }

    fun decryptData(keyAlias: String): Flow<String?> = dataStoreModule.data
        .catch {exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val iv = preferences[stringPreferencesKey("iv")]?.toByteArray() ?: return@map null
            val encryptedData = preferences[stringPreferencesKey("encryptedData")]?.toByteArray() ?: return@map null
            val secretKey = generateSecretKey(keyAlias)
            val cipher = keystoreModule.provideCipher()
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            String(cipher.doFinal(encryptedData))
        }
}