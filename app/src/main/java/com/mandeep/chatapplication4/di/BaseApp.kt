package com.mandeep.chatapplication4.di

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.SharedPreferenceManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApp:Application() {

    lateinit var firebaseMessaging: FirebaseMessaging
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    lateinit var firestore: FirebaseFirestore


    override fun onCreate() {
        super.onCreate()


        sharedPreferenceManager = SharedPreferenceManager(this)
        firebaseMessaging = FirebaseMessaging.getInstance()
        firestore = FirebaseFirestore.getInstance()

        firebaseMessaging.token.addOnSuccessListener {
            sharedPreferenceManager.putString(Constants.KEY_FCM_TOKEN,it.toString())

        }

    }

}