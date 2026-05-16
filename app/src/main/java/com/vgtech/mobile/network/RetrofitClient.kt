package com.vgtech.mobile.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * IMPORTANTE PARA EL EQUIPO:
     * Si vas a correr el servidor Ktor (vgtech-backend) en tu propia computadora,
     * debes cambiar esta IP por la IP de tu propia máquina en tu red Wi-Fi (ej. 192.168.x.x)
     * o usar "http://10.0.2.2:8080/" si estás usando el emulador de Android Studio.
     * 
     * PC IP de Adrian: 192.168.0.124  |  Puerto Ktor: 8080
     */
    private const val BASE_URL = "http://192.168.100.16:8080/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
