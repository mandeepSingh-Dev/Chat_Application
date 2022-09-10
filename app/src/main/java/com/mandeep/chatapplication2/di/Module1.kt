package com.mandeep.chatapplication2.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mandeep.chatapplication2.SharedPreferenceManager
import com.mandeep.chatapplication2.Utils.FCMApiService
import com.mandeep.chatapplication2.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Module1 {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSharedPrefernceManager(@ApplicationContext context: Context) = SharedPreferenceManager(context)


    @Provides
   fun provideMainDispatcher() = Dispatchers.Main


    val BASE_URL = "https://pixabay.com"
    @Provides
    fun provideRetrofit() = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)


    @Provides
    fun provideFCM() = FirebaseMessaging.getInstance()

    private val BASE_URL2 = "https://fcm.googleapis.com/fcm/"

    @Provides
    fun provideRetrofitt() = Retrofit.Builder().baseUrl(BASE_URL2).addConverterFactory(GsonConverterFactory.create()).build().create(FCMApiService::class.java)
}