package com.akoscz.youtubechannels.di

import android.content.Context
import com.akoscz.youtubechannels.BuildConfig
import com.akoscz.youtubechannels.data.db.AppSettingsManager
import com.akoscz.youtubechannels.data.network.MockYoutubeApiService
import com.akoscz.youtubechannels.data.network.YoutubeApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) { // Enable logging only for debug builds
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient) // Use the OkHttpClient with logging interceptor
            .build()
    }

    @Provides
    @Singleton
    fun provideYoutubeApiService(
        @ApplicationContext context: Context,
        retrofit: Retrofit,
        appSettingsManager: AppSettingsManager
    ): YoutubeApiService {
        return if (appSettingsManager.isMockDataEnabled()) {
            println("provideYoutubeApiService: Using mock data")
            MockYoutubeApiService(context)
        } else {
            println("provideYoutubeApiService: Using real data")
            retrofit.create(YoutubeApiService::class.java)
        }
    }
}
