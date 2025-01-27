package com.nametag.nametagduckittest.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

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

class NoNetworkException(message: String? = null) : IOException(message)