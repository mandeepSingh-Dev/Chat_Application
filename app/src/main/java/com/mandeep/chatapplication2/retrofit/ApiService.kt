package com.mandeep.chatapplication2.retrofit

import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.retrofit.DataClass.Image
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("/api/?safesearch=true&orientation=vertical&per_page=50&category=food")
     suspend fun fetchData(@Query("key") key:String): Image
}