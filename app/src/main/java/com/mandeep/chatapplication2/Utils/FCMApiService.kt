package com.mandeep.chatapplication2.Utils

import com.mandeep.chatapplication2.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMApiService {

    @Headers("Authorization: key=" + Constants.SERVER_KEY, "Content-Type: application/json")
    @POST("/send")
    fun sendData(/*@HeaderMap map:HashMap<String,String>,*/ @Body messageBody:String): Call<String>
}