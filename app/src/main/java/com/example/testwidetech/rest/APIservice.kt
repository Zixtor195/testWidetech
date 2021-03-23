package com.example.testwidetech.rest

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Url

interface APIservice {
    @POST
    fun getProducts(@Url url:String): Call<List<TestData>>
}