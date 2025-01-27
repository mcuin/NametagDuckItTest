package com.nametag.nametagduckittest.utils

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * Retrofit module for providing the Retrofit instance through dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    /**
     * Provides the base URL for the Retrofit instance.
     * @return The base URL for the Retrofit instance.
     */
    @Provides
    fun provideBaseUrl() = "https://nametag-duckit-2.uc.r.appspot.com/"

    @Provides
    fun provideNetworkMonitor(@ApplicationContext context: Context) = LiveNetworkMonitor(context) as NetworkMonitor

    /**
     * Provides the Retrofit instance.
     * @param baseUrl The base URL for the Retrofit instance.
     * @return The Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, liveNetworkMonitor: NetworkMonitor): Retrofit {
        val monitor = OkHttpClient.Builder().addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor)).build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                Json.asConverterFactory("application/json".toMediaType())
            )
            .client(monitor)
            .build()
    }

    /**
     * Provides the API service for the Retrofit instance.
     * @param retrofit The Retrofit instance.
     * @return The API service for the Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(APIService::class.java)
}