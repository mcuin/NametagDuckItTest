package com.nametag.nametagduckittest.utils

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Module for the data store.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provides the data store.
     * @param context The context of the application provided by Hilt
     * @return The data store
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile("DuckItPreferences") })
}