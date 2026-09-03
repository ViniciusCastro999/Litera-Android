package com.litera.app.core.di

import com.litera.app.BuildConfig
import com.litera.app.core.common.Constants
import com.litera.app.data.remote.GoogleBooksApiService
import com.litera.app.data.remote.WikipediaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(Constants.GOOGLE_BOOKS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleBooksApiService(retrofit: Retrofit): GoogleBooksApiService =
        retrofit.create(GoogleBooksApiService::class.java)

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideWikipediaApiService(okHttpClient: OkHttpClient): WikipediaApiService {
        val contentType = "application/json".toMediaType()
        // Built and consumed locally (not exposed as a plain `Retrofit`
        // binding) so it doesn't collide with the Google Books Retrofit above.
        return Retrofit.Builder()
            .baseUrl(Constants.WIKIPEDIA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(WikipediaApiService::class.java)
    }
}
