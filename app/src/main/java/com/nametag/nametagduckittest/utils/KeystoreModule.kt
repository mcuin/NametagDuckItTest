package com.nametag.nametagduckittest.utils

import android.security.keystore.KeyProperties
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.inject.Singleton

/**
 * Module for the keystore.
 */
@Module
@InstallIn(SingletonComponent::class)
object KeystoreModule {

    /**
     * Provides the keystore.
     * @return The keystore
     */
    @Provides
    @Singleton
    fun provideKeyStore(): KeyStore {
        return KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    /**
     * Provides the key generator.
     * @return The key generator
     */
    @Provides
    @Singleton
    fun provideKeyGenerator(): KeyGenerator {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    }

    /**
     * Provides the cipher.
     * @return The cipher
     */
    @Provides
    @Singleton
    fun provideCipher(): Cipher = Cipher.getInstance("AES/GCM/NoPadding")
}