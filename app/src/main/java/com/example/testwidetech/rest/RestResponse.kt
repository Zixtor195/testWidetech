package com.example.testwidetech.rest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class TestData (val Description:String, val ImageUrl:String, val Name:String)

data class UserInfo (val userEmail: String,  val provider: String)
