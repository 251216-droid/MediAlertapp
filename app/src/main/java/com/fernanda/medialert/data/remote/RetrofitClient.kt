package com.fernanda.medialert.data.remote

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
object RetrofitClient {

    //private const val BASE_URL = "http://192.168.0.27:3000/"
    private const val BASE_URL = "https://medialert1-production.up.railway.app/"

    val instance: Retrofit by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy {
        instance.create(AuthApiService::class.java)
    }

    // Servicio para Enfermedades
    val enfermedadService: EnfermedadApiService by lazy {
        instance.create(EnfermedadApiService::class.java)
    }

    // Servicio para Medicamentos y Programaci�n
    val medicamentoService: MedicamentoApiService by lazy {
        instance.create(MedicamentoApiService::class.java)
    }

    // Servicio para Historial y Pr�ximas Dosis
    val historialService: HistorialApiService by lazy {
        instance.create(HistorialApiService::class.java)
    }
}

