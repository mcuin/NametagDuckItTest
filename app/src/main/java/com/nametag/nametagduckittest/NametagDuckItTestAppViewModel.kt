package com.nametag.nametagduckittest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nametag.nametagduckittest.utils.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for the app.
 * @param dataStoreRepository The data store repository to check if the user is logged in.
 */
@HiltViewModel
class NametagDuckItTestAppViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    /**
     * Logout the user by calling the logout function in the data store repository.
     */
    fun logout() {
        viewModelScope.launch {
            dataStoreRepository.logout()
        }
    }
}