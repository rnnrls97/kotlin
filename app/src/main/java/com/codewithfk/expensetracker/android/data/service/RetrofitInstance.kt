package com.codewithfk.expensetracker.android.data.service

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PicSumApiService {
    @GET("600")
    suspend fun getRandomImage(@Query("random") random: Long): ResponseBody
}

object RetrofitInstance {
    private const val BASE_URL = "https://picsum.photos/"

    val api: PicSumApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicSumApiService::class.java)
    }
}
