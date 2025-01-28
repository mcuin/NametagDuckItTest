package com.nametag.nametagduckittest.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Class to check if there is a network connection.
 * @param networkMonitor The network monitor to check if there is a network connection.
 */
class NetworkMonitorInterceptor @Inject constructor(private val networkMonitor: NetworkMonitor) :
    Interceptor {

        @Throws(NoNetworkException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            if (!networkMonitor.isConnected()) {
                throw NoNetworkException()
            }
            return chain.proceed(chain.request())
        }
}

/**
 * Exception thrown when there is no network connection.
 */
class NoNetworkException(message: String? = null) : IOException(message)