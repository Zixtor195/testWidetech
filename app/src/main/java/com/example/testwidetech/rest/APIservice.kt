package com.example.testwidetech.rest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface APIservice {
    @POST
    fun getProducts(@Url url:String): Call<List<TestData>>


    @Headers("Content-Type: application/json")
    @POST
    fun addUser(@Url url:String, @Body userInfo:UserInfo): Call <String>
}