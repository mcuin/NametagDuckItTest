package com.nametag.nametagduckittest.utils

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
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
class NoNetworkException(val code : Int = 500, val response: ResponseBody = "".toResponseBody(null), message: String? = null) : IOException(message)