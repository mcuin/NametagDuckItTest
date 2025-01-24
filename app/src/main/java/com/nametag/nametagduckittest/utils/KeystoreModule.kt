package com.nametag.nametagduckittest.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeystoreModule {

    @Provides
    @Singleton
    fun provideKeyStore(): KeyStore {
        return KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    @Provides
    @Singleton
    fun provideKeyGenerator(): KeyGenerator {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    }

    @Provides
    @Singleton
    fun provideCipher(): Cipher = Cipher.getInstance("AES/GCM/NoPadding")

    @Provides
    @Singleton
    fun provideSecretKey(keyAlias: String): SecretKey {
        val keyEntry = provideKeyStore().getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry

        return keyEntry?.secretKey ?: run {
            provideKeyGenerator().apply {
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
}