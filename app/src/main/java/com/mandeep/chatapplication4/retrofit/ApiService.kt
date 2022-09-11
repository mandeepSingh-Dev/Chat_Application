package com.mandeep.chatapplication4.retrofit

import com.mandeep.chatapplication4.retrofit.DataClass.Image
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/api/?safesearch=true&orientation=vertical&per_page=50&category=food")
     suspend fun fetchData(@Query("key") key:String): Image
}