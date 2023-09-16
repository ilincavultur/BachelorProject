package com.example.digitaldiaryba.dependencyinjection

import android.app.Application
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.example.digitaldiaryba.data.api.*
import com.example.digitaldiaryba.data.database.Database
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.FormBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext appContext: Context
    ) = Room.databaseBuilder(
        appContext,
        Database::class.java, "digital_diary"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideAlbumObjectDao(
        database: Database
    ) = database.albumObjectDao()

    @Singleton
    @Provides
    fun provideMediaObjectDao(
        database: Database
    ) = database.mediaObjectDao()

    @Singleton
    @Provides
    fun providePresentationObjectDao(
        database: Database
    ) = database.presentationObjectDao()

    @Singleton
    @Provides
    fun provideBuildingInfoDao(
        database: Database
    ) = database.buildingInfoDao()

    @Singleton
    @Provides
    fun provideNearbyLandmarkDao(
        database: Database
    ) = database.nearbyLandmarkDao()

    @Singleton
    @Provides
    fun provideRetrofit(): DBpediaService = Retrofit.Builder()
        .baseUrl("https://dbpedia.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DBpediaService::class.java)

    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext appContext: Context
    ) = appContext

    @Singleton
    @Provides
    fun provideGoogleOAuthService(): GoogleOAuthApiService = Retrofit.Builder()
        .baseUrl("https://accounts.google.com/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleOAuthApiService::class.java)

    @Singleton
    @Provides
    fun provideGoogleVisionApiService(): VisionApi = Retrofit.Builder()
        .baseUrl("https://vision.googleapis.com/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(VisionApi::class.java)

    @Singleton
    @Provides
    fun provideFoursquareApiService(): FoursquareApi = Retrofit.Builder()
        .baseUrl("https://api.foursquare.com/v3/places/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FoursquareApi::class.java)

    @Singleton
    @Provides
    fun provideWikipediaApiService(): WikipediaApi = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/api/rest_v1/page/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WikipediaApi::class.java)

}

