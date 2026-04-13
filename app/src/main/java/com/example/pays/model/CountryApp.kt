package com.example.pays.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface CountryApp {
    @GET("v3.1/lang/french")
    suspend fun getAllCountries(): List<Country>

    @GET("v3.1/region/africa")
    suspend fun getAfricanCountries(): List<Country>

    companion object {
        private const val BASE_URL = "https://restcountries.com/"

        fun create(): CountryApp {
            // Ajout d'un client OkHttp avec des timeouts plus longs pour l'émulateur
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CountryApp::class.java)
        }
    }
}