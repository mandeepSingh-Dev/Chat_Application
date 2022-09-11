package com.mandeep.chatapplication4.MyService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication4.Connectivity.MyConnectivityManager
import com.mandeep.chatapplication4.Connectivity.State
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MessageService : Service() {

    lateinit var notificationManager :NotificationManager
    lateinit var manager : MyConnectivityManager
    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore


    val NOTIFICATION_ID = 100
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager = MyConnectivityManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            val user_id = intent.getStringExtra(Constants.KEY_USER_ID).toString()

            CoroutineScope(Dispatchers.Main).launch {
                manager.observe().collect {
                    Log.d("dfdignfdg",it.toString())
                    when (it) {
                        State.Available -> updateNetwork(user_id = user_id, con = true)
                        else -> updateNetwork(user_id,false)
                    }
                }


            }
/*
            firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).addSnapshotListener { value, error ->
                value?.documents?.forEach {
                    if(user_id.equals(it.getString(Constants.KEY_RECEIVER_ID)))
                    {
                        Log.d("dfidngdff",user_id+"   "+it.getString(Constants.KEY_RECEIVER_ID))
                       if(it.getBoolean(Constants.KEY_IS_REACHED) == false)
                       {
                           firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(it.id).update(Constants.KEY_IS_REACHED,true)
                       }
                    }
                }

            }
            CoroutineScope(Dispatchers.Main).launch {
                for(i in 0..10)
                {
                    delay(1000)
                    Log.d("fidfngdf",i.toString())
                }
            }*/
            /*firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).get().addOnSuccessListener {
                it.documents.forEach {

               }*/
   // }
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
       return null
    }


    fun showNotification(){
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val notificationChannel = NotificationChannel("MessageNotification","Message",NotificationManager.IMPORTANCE_HIGH)

             notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this,"MessageNotification")
            .setSmallIcon(R.drawable.ic_baseline_check_circle_24)
            .setContentTitle("Incoming Notification")
            .build()

        notificationManager.notify(NOTIFICATION_ID,notification)
    }

    fun updateNetwork(user_id:String,con:Boolean){
        try {
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(user_id)
                .update(Constants.KEY_NETWORK, con)
            Toast.makeText(this, con.toString(), Toast.LENGTH_SHORT).show()
            Log.d("difndgd", con.toString())
        }catch (e:Exception){
            Log.d("ikfngg",e.message.toString())
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
       // startService(rootIntent)
    }
}