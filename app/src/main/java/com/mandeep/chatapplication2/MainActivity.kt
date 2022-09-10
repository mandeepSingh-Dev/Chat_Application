package com.mandeep.chatapplication2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.mandeep.chatapplication2.Connectivity.MyConnectivityManager
import com.mandeep.chatapplication2.Firebase.Adapters.ChatAdapter
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.MyService.ClosingService
import com.mandeep.chatapplication2.MyService.MessageService
import com.mandeep.chatapplication2.Utils.FCMApiService
import com.mandeep.chatapplication2.Utils.FCMHttpServerRequest
import com.mandeep.chatapplication2.databinding.ActivityMainBinding
import com.mandeep.chatapplication2.fragment.ChatsFragment
import com.squareup.okhttp.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),ChatsFragment.GettingFragmentInstanceListener {

    val firebaseViewmodel by viewModels<FirebaseViewmodel>()
    lateinit var binding:ActivityMainBinding
    lateinit var findNavController:NavController

    @Inject
    lateinit var sharedPreferenceManager :SharedPreferenceManager
    @Inject
    lateinit var firebaseStore:FirebaseFirestore
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    @Inject
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var navController:NavController

    var userColllection:CollectionReference?=null

    private var user_id :String?=null


    var intentt :Intent?=null
    var intent1:Intent?=null
    var closingService : ClosingService?=null
    lateinit var notificationManager : NotificationManager


    val REMOTE_MSG_AUTHORIZATION = "Authorization"
    val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    val REMOTE_MSG_DATA = "data"
    val REMOTE_MSG_REGISTRAION_IDS = "registration_ids"

    val KEY_FCM_TOKEN = "token"
    val KEY_USER = "user"


    val serviceConnection = object:ServiceConnection
    {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
           val myBinder = binder as ClosingService.MyBinder
            closingService = myBinder.getService()
            Log.d("dif3443434ndf",closingService.toString())

            Log.d("9g3g3gv",closingService.toString())
            Log.d("eifndf",firebaseStore.toString())
            closingService?.setFireFireStore(firebaseStore)
            closingService?.setUserIdd(sharedPreferenceManager)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
        }
    }


   // private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "USER_DATASTORE")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseStore.collection(Constants.KEY_COLLECTION_CHAT).get().addOnSuccessListener {
            Log.d("dkmdfgdf043r3",it.size().toString())
        }

        Log.d("fdigndfg","kdivndf")


        /**  val name_key = stringPreferencesKey("Name")

        lifecycleScope.launch{
            dataStore.edit {
                it[name_key] = "hello"
            }
        }
        dataStore.data.map {
            val name = it[name_key]?:""
            Log.d("fimfgfg",name.toString())
        }*/

      Log.d("fogmfgf",URLUtil.isValidUrl("www.google.com").toString())

        val map = hashMapOf("username" to "Mandeep singh")
        firebaseStore.collection(Constants.KEY_COLLECTION_CHAT).add(map).addOnCompleteListener {
            Log.d("dknigf",it.result.toString())
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        user_id = sharedPreferenceManager.getString(Constants.KEY_USER_ID).toString()
        userColllection = firebaseStore.collection(Constants.KEY_COLLECTION_USER)

        // starting MessageServie
        intent1 = Intent(this,MessageService::class.java)
        intent1?.putExtra(Constants.KEY_USER_ID, user_id)
        startService(intent1)

        // Log.d("dfodmgd",URLUtil.i("www.google.com").toString())


        /*  val intent = Intent(this,NotificationListenerService::class.java)
          startService(intent)*/

        intentt = Intent(this,ClosingService::class.java)
        startService(intentt)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        findNavController = navHostFragment.navController



        lifecycleScope.launch {
            val manager = MyConnectivityManager(this@MainActivity)
            manager.observe().collect{
                Toast.makeText(this@MainActivity,it.toString(),Toast.LENGTH_SHORT).show()
                Log.d("e334343r3r",it.toString())
            }
        }


        /**THIS CODE IS FOR UPDATE some fields  for chats */
        lifecycleScope.launch {

            Log.d("digdngdg","xAiwOLP5mMekrVMoWO5T")
                 firebaseStore.collection(Constants.KEY_COLLECTION_CHAT).get().addOnSuccessListener {
                     it.documents.forEach { documentSnalshot->
                         Log.d("digdngdg",documentSnalshot.id.toString())
                         Log.d("digdngdg",documentSnalshot.id.trim().toString())

                         val id = documentSnalshot.id.trim()
                         Log.d("ifnbfgv",id.toString())
                         firebaseStore.collection(Constants.KEY_COLLECTION_CHAT).document(id).update(Constants.KEY_DOC_ID ,id).addOnSuccessListener {
                             Log.d("odgdfd",id.toString()+"TRUE")
                         }

                     }


                 }
          //  }
        }

        CoroutineScope(Dispatchers.Main).launch {
            firebaseViewmodel.chatList.collect{
                it.forEach {

                }
            }
        }

     /*   val bundle = Bundle()
        bundle.putString("token","c5n6b7-rTNq5WU1wsMFKp0:APA91bHY8dtD8UwyuKiq-ZAT5PTW4a3YlbumB6jhI-vSyCQ3-g6sIEpmWuCT8STFOxXl9mzfJ_3OGUWncGO4vCdzK21G7CbVV6dUGllxU80dCafyDlrNkI6t7srGTJXPNUHbW-2HrCIg")
        bundle.putString("title","TITLE")
        bundle.putString("to","c5n6b7-rTNq5WU1wsMFKp0:APA91bHY8dtD8UwyuKiq-ZAT5PTW4a3YlbumB6jhI-vSyCQ3-g6sIEpmWuCT8STFOxXl9mzfJ_3OGUWncGO4vCdzK21G7CbVV6dUGllxU80dCafyDlrNkI6t7srGTJXPNUHbW-2HrCIg")
*/
        val rm = RemoteMessage.Builder("c5n6b7-rTNq5WU1wsMFKp0:APA91bHY8dtD8UwyuKiq-ZAT5PTW4a3YlbumB6jhI-vSyCQ3-g6sIEpmWuCT8STFOxXl9mzfJ_3OGUWncGO4vCdzK21G7CbVV6dUGllxU80dCafyDlrNkI6t7srGTJXPNUHbW-2HrCIg")
        rm.data = hashMapOf("token" to "c5n6b7-rTNq5WU1wsMFKp0:APA91bHY8dtD8UwyuKiq-ZAT5PTW4a3YlbumB6jhI-vSyCQ3-g6sIEpmWuCT8STFOxXl9mzfJ_3OGUWncGO4vCdzK21G7CbVV6dUGllxU80dCafyDlrNkI6t7srGTJXPNUHbW-2HrCIg",
        "title" to "TITLE",
        )
        firebaseMessaging.send(rm.build())





        binding.button.setOnClickListener{
           // FCMHttpServerRequest.sendNotification()
           // sendNotification_through_Retrofit()
        }




    }
    @Inject
    lateinit var FCMApiServicefirebase : FCMApiService

    override fun onStart() {
        super.onStart()
        bindService(intentt,serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("difndf",closingService.toString())
    }

    override fun onStop() {
        super.onStop()
        Log.d("dfindfgd","onStop")
        user_id?.let {
            if(it.isNotEmpty()) {
                Log.d("ignfgf",it.toString())
                userColllection?.document(it)?.update(Constants.KEY_ID, System.currentTimeMillis().toString())
                userColllection?.document(it)?.update(Constants.KEY_IS_ONLINE, false)
            }
        }
    }

    override fun onResume() {
        super.onResume()



        user_id?.let {
            if(it.isNotEmpty()) {
                Log.d("dfomdgdf",it.toString())
                userColllection?.document(it)?.update(Constants.KEY_ID, System.currentTimeMillis().toString())
                userColllection?.document(it)?.update(Constants.KEY_IS_ONLINE, true)

                userColllection?.document(it)?.update(Constants.KEY_USER_CLOUD_TOKEN, sharedPreferenceManager.getString(Constants.KEY_FCM_TOKEN).toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("difndgfd","onDestory")


        user_id?.let {
            if(it.isNotEmpty()) {
                userColllection?.document(it)?.update(Constants.KEY_ID, System.currentTimeMillis().toString())
                userColllection?.document(it)?.update(Constants.KEY_IS_ONLINE, false)
            }
        }
        stopService(intentt)
        unbindService(serviceConnection)

        firebaseMessaging.unsubscribeFromTopic("/topics/mytopic10")

    }

    override fun onBackPressed() {
        // super.onBackPressed()
        Log.d("dfidnfd","bina wala")
        if(sharedPreferenceManager.getBoolean(Constants.IS_SIGNED_IN)){

            if(findNavController.currentDestination?.id == R.id.usersFragment){
                Log.d("54545454","login wala")
                finish()
                super.onBackPressed()
            }
            else if( findNavController.currentDestination?.id == R.id.chatsFragment)
            {
                Log.d("dkfndfd",selectedlistSize.toString())
                if(selectedlistSize >= 1)
                {
                    Log.d("fidnf3f3",chatFragment.sample.toString())
                    chatFragment.setBackPressed(true)
                }else{
                    super.onBackPressed()
                }

            }
            else{
                Log.d("dfidnfd","bina login wala")
                super.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }
    }
    var selectedlistSize:Int=0

   lateinit var chatAdapter: ChatAdapter
    fun setSelectedListSize(size:Int,chatAdapter: ChatAdapter)
    {
        selectedlistSize = size
        this.chatAdapter = chatAdapter
    }

    lateinit var chatFragment: ChatsFragment
    override fun getFragmentInstance(fragment: Fragment) {
        chatFragment = fragment as ChatsFragment
    }

    fun showIncomingMessageNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           val notificationChannel =  NotificationChannel("incoming_message_Channel","title",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)

            val notification = NotificationCompat.Builder(this,"incoming_message_Channel")
                .setSmallIcon(R.drawable.ic_baseline_check_circle_24)
                .setContentText("Title")
                .build()

            notificationManager.notify(100,notification)
        }
    }

    fun update_IS_REACHED_KEY(){
        lifecycleScope.launch{
            firebaseViewmodel.chatList.collect{ chatlist->
                chatlist.forEach { chat ->
                    if(user_id?.equals(chat.receiverId)!!) {
                        firebaseStore.collection(Constants.KEY_COLLECTION_CHAT).document(chat.doc_id.toString()).update(Constants.KEY_IS_REACHED, true)
                    }
                }

            }
        }
    }
    val fcmApiService = Retrofit.Builder().baseUrl("https://fcm.googleapis.com/fcm/").addConverterFactory(GsonConverterFactory.create()).build().create(FCMApiService::class.java)
    fun sendNotification_through_Retrofit(){
        val remoteMsgHeader = hashMapOf(REMOTE_MSG_AUTHORIZATION to "key=AAAAB2nPLx4:APA91bHlj71C80-RxT7Tyrk2jgRPKq9IfVrnRpZOVR6NAiX50JfDrg3OVL7KQJ4B_5y1uaCM6eTx_TGG4G514lNTQg448RfZj4POauYtq0RXkbzAwYEDjstJerFK67Fkt8FSpQTUTgPO",
            REMOTE_MSG_CONTENT_TYPE to "application/json")


        val receiverUserToken = "dm47KRQiREmvDvjSao9-cl:APA91bF-HcUIMwyZ7fv5WyoYR8O8LDIhddtIlJWlO4uiQUtj2RfI6-CM7-GdRoyXoHfsSK3h087UDmxAvaeUfkP7XO04nkq1pDW4L8cN0GywKLfyRatjc2Ymc5ryBTDy7GwWwwwFqHxf"


        val data = JSONObject()
        data.put("title", "Notification title")
        data.put("content", "Content")

        val body = JSONObject()
        body.put("data",data.toString())
        body.put("to",receiverUserToken)



        fcmApiService.sendData(/*remoteMsgHeader,*/body.toString()).enqueue(object: retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("dfidngd",call.request().body.toString())
                Log.d("94ht4g4",response.body().toString())
                Log.d("dgfuidnfd","success")
                Log.d("dfidufnd",sharedPreferenceManager.getString(Constants.KEY_FCM_TOKEN).toString())
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("dfdbgdf",t.message.toString())
            }
        })
    }


    fun sendNotification_through_HttpUrlConn(){

        val server_key = "AAAAB2nPLx4:APA91bHlj71C80-RxT7Tyrk2jgRPKq9IfVrnRpZOVR6NAiX50JfDrg3OVL7KQJ4B_5y1uaCM6eTx_TGG4G514lNTQg448RfZj4POauYtq0RXkbzAwYEDjstJerFK67Fkt8FSpQTUTgPO"
        val receiverUserToken = "dm47KRQiREmvDvjSao9-cl:APA91bF-HcUIMwyZ7fv5WyoYR8O8LDIhddtIlJWlO4uiQUtj2RfI6-CM7-GdRoyXoHfsSK3h087UDmxAvaeUfkP7XO04nkq1pDW4L8cN0GywKLfyRatjc2Ymc5ryBTDy7GwWwwwFqHxf"

        /*firebaseMessaging.subscribeToTopic("mytopic").addOnSuccessListener {
            Log.d("dfubgbdfv","success")
        }.addOnFailureListener {
            Log.d("dfidngfd",it.message+"    "+it.cause.toString())
            Log.d("dfudfnf","fialure")
        }*/

        CoroutineScope(Dispatchers.IO).launch {
            val endpoint = "https://fcm.googleapis.com/fcm/send"
            val endpoint2 = "https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send"
            try {
                val url = URL(endpoint)
                val httpsURLConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                httpsURLConnection.readTimeout = 10000
                httpsURLConnection.connectTimeout = 15000
                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = true

                // Adding the necessary headers
                httpsURLConnection.setRequestProperty("Authorization", "key=$server_key")
                httpsURLConnection.setRequestProperty("Content-Type", "application/json")

                // Creating the JSON with post params

                val data = JSONObject()
                data.put("title", "Notification title")
                data.put("content", "Content")

                val body = JSONObject()
                body.put("data",data)
                body.put("to",receiverUserToken/*"/topics/mytopic21"*/)

                val outputStream: OutputStream = BufferedOutputStream(httpsURLConnection.outputStream)
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
                writer.write(body.toString())
                writer.flush()
                writer.close()
                outputStream.close()


              /*  val responseCode: Int = httpsURLConnection.responseCode
                val responseMessage: String = httpsURLConnection.responseMessage

                Log.d("Respon777se:", "$responseCode $responseMessage")

                var result = String()
                var inputStream: InputStream? = null
                inputStream = if (responseCode in 400..499) {
                    httpsURLConnection.errorStream
                } else {
                    httpsURLConnection.inputStream
                }

                if (responseCode == 200) {
                    Log.d("Successdswewes:", "notification sent $title \n Content")
                    // The details of the user can be obtained from the result variable in JSON format
                } else {
                    Log.d("Errerereror", "Error Response")
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }

            }
        }
    }

