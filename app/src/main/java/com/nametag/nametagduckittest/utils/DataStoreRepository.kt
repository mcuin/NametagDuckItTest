package com.nametag.nametagduckittest.utils

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(private val dataStoreModule: DataStore<Preferences>) {

    fun isLoggedInFlow(): Flow<Boolean> = dataStoreModule.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            println(preferences.asMap())
            println(preferences[stringPreferencesKey("encryptedData")] != null)
            preferences[stringPreferencesKey("encryptedData")] != null
        }

    suspend fun logout() {
        dataStoreModule.edit {
            it.clear()
        }
    }
}