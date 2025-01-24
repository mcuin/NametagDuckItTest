package com.nametag.nametagduckittest

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.DataStoreModule
import com.nametag.nametagduckittest.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NametagDuckItTestAppViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    val isLoggedInFlow = dataStoreRepository.isLoggedInFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun logout() {
        viewModelScope.launch {
            dataStoreRepository.logout()
        }
    }
}