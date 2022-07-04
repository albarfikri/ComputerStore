package com.albar.computerstore.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.albar.computerstore.others.Constants.LOCAL_SHARED_PREF
import com.albar.computerstore.others.DataStoreUtility
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreContext(@ApplicationContext appContext: Context): DataStoreUtility =
        DataStoreUtility(appContext)

    @SuppressLint("VisibleForTests")
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext app: Context) =
        FusedLocationProviderClient(app)

    @Provides
    @Singleton
    fun provideGlideContext(@ApplicationContext context: Context): RequestManager =
        Glide.with(context)


    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
}