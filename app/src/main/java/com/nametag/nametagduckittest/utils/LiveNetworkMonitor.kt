package com.nametag.nametagduckittest.utils

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

/**
 * Interface for the network monitor.
 */
interface NetworkMonitor {
    fun isConnected(): Boolean
}

/**
 * Implementation of the network monitor.
 * @param context The application context.
 */
class LiveNetworkMonitor @Inject constructor(context: Context) : NetworkMonitor {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork
        return network != null
    }
}