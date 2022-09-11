package com.mandeep.chatapplication4.MyService

import android.app.Service
import android.content.Intent
import android.os.Binder

import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.SharedPreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ClosingService : Service() {

    @Inject
    lateinit var firebaseFirestore1:FirebaseFirestore

    @Inject
    lateinit var sharedPreferenceManager1:SharedPreferenceManager

    var myBinder : MyBinder = MyBinder()
    var firebaseFirestore :FirebaseFirestore?=null
    @Nullable
   override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }
    
    inner class MyBinder: Binder() {
        fun getService() = this@ClosingService
   }


    fun setFireFireStore(firebaseFirestore: FirebaseFirestore){
        this.firebaseFirestore = firebaseFirestore
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }
   override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)


        // Handle application closing

       CoroutineScope(Dispatchers.Main).launch {
         //  firebaseFirestore1.collection(Constants.KEY_COLLECTION_USER)?.document("WH6IAaCxXpUlgwavhwyT")?.update(Constants.KEY_ID, System.currentTimeMillis().toString())
           //delay(100)
        //   firebaseFirestore1.collection(Constants.KEY_COLLECTION_USER)?.document("WH6IAaCxXpUlgwavhwyT")?.update(Constants.KEY_IS_ONLINE, false)
       }


       val userId =  sharedPreferenceManager1.getString(Constants.KEY_USER_ID).toString()
       Log.d("didngdf",userId)
       Log.d("fndfdf",firebaseFirestore.toString())

Log.d("sdikndgfd","dfdnfd")
        // Destroy the service
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()

        val userId =  sharedPreferenceManager1.getString(Constants.KEY_USER_ID).toString()
        Log.d("didng33df",userId)
        Log.d("fndf33df",firebaseFirestore1.toString())

        CoroutineScope(Dispatchers.Main).launch {
            firebaseFirestore1.collection(Constants.KEY_COLLECTION_USER)?.document("WH6IAaCxXpUlgwavhwyT")?.update(Constants.KEY_ID, System.currentTimeMillis().toString())
            delay(100)
            firebaseFirestore1.collection(Constants.KEY_COLLECTION_USER)?.document("WH6IAaCxXpUlgwavhwyT")?.update(Constants.KEY_IS_ONLINE, false)
        }
        stopSelf()
    }

    var sharedPreferenceManager : SharedPreferenceManager?=null
    fun setUserIdd(sharedPreferenceManager : SharedPreferenceManager) {
         this.sharedPreferenceManager = sharedPreferenceManager
    }
}