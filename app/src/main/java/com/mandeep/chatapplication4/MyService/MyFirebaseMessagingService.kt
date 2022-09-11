package com.mandeep.chatapplication4.MyService

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class MyFirebaseMessagingService :FirebaseMessagingService() {


    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onNewToken(token: String) {
        super.onNewToken(token)


        Log.d("dfid65656ngd",token.toString())
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notifcationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =  NotificationChannel("notificationChannel","Channel",NotificationManager.IMPORTANCE_HIGH)

            notifcationManager.createNotificationChannel(notificationChannel)

            val builder = NotificationCompat.Builder(this,"notificationChannel")
                .setSmallIcon(R.drawable.ic_input_add)
                .setContentTitle(remoteMessage.data.get("title").toString())
                .setContentText(remoteMessage.data.get("content").toString())
            
              //  .setStyle()

            notifcationManager.notify(1,builder.build())

        }



        Log.d("dgonfdgfd",remoteMessage.toString())

        Log.d("div3333ndf",remoteMessage.data.keys.toString())

        val chat_doc_Id = remoteMessage.data.get(Constants.KEY_DOC_ID).toString()
        Log.d("difinhdkfd",chat_doc_Id+"   EMPTYY")


        update_Is_Reached_Field(chat_doc_Id)

        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        // Second case when notification payload is
        // received.
        Log.d("dgindgfd",remoteMessage.data.get("title").toString())


        // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
          //  showNotification("Title","Content")

    }

    // Method to get the custom Design for the display of
    // notification.
    @SuppressLint("RemoteViewLayout")
    private fun getCustomDesign(title: String?, message: String?): RemoteViews? {
        val remoteViews = RemoteViews(applicationContext.packageName, com.mandeep.chatapplication4.R.layout.story_item)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.drawable.ic_menu_close_clear_cancel
        )
        return remoteViews
    }

    // Method to display the notifications
    fun showNotification(title: String?, message: String?) {
        // Pass the intent to switch to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Assign channel ID
        val channel_id = "notification_channel"
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channel_id)
            .setSmallIcon(R.drawable.ic_input_add)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(getCustomDesign(title, message))
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }

    /*override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
Log.d("dfgifnhfhg",message.messageId.toString()+"cfmbfoib")
        Toast.makeText(this,message.messageId.toString(),Toast.LENGTH_SHORT).show()
    }

   *//* override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }*//*

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("kfndfd",token)
    }*/


    fun update_Is_Reached_Field(chat_doc_Id:String){
        firestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat_doc_Id).update(Constants.KEY_IS_REACHED,true).addOnSuccessListener {
            firestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat_doc_Id).update(Constants.KEY_IS_SENT, false)
            firestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat_doc_Id).update(Constants.KEY_IS_SEEN, false)
        }
    }
}