package com.mandeep.chatapplication4.Utils

import android.util.Log
import com.mandeep.chatapplication4.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object FCMHttpServerRequest {

    fun sendNotification(receiverUserToken:String,username:String,message:String,doc_id:String){

        Log.d("fgnfg",receiverUserToken.toString())
        val server_key = "AAAAB2nPLx4:APA91bHlj71C80-RxT7Tyrk2jgRPKq9IfVrnRpZOVR6NAiX50JfDrg3OVL7KQJ4B_5y1uaCM6eTx_TGG4G514lNTQg448RfZj4POauYtq0RXkbzAwYEDjstJerFK67Fkt8FSpQTUTgPO"
        val receiverUserToken1 = "dm47KRQiREmvDvjSao9-cl:APA91bF-HcUIMwyZ7fv5WyoYR8O8LDIhddtIlJWlO4uiQUtj2RfI6-CM7-GdRoyXoHfsSK3h087UDmxAvaeUfkP7XO04nkq1pDW4L8cN0GywKLfyRatjc2Ymc5ryBTDy7GwWwwwFqHxf"

        /*firebaseMessaging.subscribeToTopic("mytopic").addOnSuccessListener {
            Log.d("dfubgbdfv","success")
        }.addOnFailureListener {
            Log.d("dfidngfd",it.message+"    "+it.cause.toString())
            Log.d("dfudfnf","fialure")
        }*/

        CoroutineScope(Dispatchers.IO).launch {
            val endpoint = "https://fcm.googleapis.com/fcm/send"
            try {
                val url = URL(endpoint)
                val httpsURLConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                httpsURLConnection.readTimeout = 10000
                httpsURLConnection.connectTimeout = 15000
                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = true

                // Adding the necessary headers
                httpsURLConnection.setRequestProperty("Authorization", "key=${Constants.SERVER_KEY1}")
                httpsURLConnection.setRequestProperty("Content-Type", "application/json")

                // Creating the JSON with post params

                val data = JSONObject()
                data.put("title", username)
                data.put("content", message)
                data.put(Constants.KEY_DOC_ID,doc_id.toString())

                val body = JSONObject()
                body.put("data",data)
                body.put("to",receiverUserToken/*"/topics/mytopic21"*/)

                val outputStream: OutputStream = BufferedOutputStream(httpsURLConnection.outputStream)
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
                writer.write(body.toString())
                writer.flush()
                writer.close()
                outputStream.close()

                  val responseCode: Int = httpsURLConnection.responseCode
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
                      Log.d("Successdswewes:", "notification sent tile \n Content")
                      // The details of the user can be obtained from the result variable in JSON format
                  } else {
                      Log.d("Errerereror", "Error Response")
                  }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}