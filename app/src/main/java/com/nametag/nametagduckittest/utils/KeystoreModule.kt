package com.nametag.nametagduckittest.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
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
}