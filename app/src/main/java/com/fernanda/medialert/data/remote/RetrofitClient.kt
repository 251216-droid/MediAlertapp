package com.fernanda.medialert.data.remote

import com.fernanda.medialert.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val DEFAULT_BASE_URL = "https://medialert1-production.up.railway.app/"
    private val baseUrl = BuildConfig.API_BASE_URL.ifBlank { DEFAULT_BASE_URL }

    val instance: Retrofit by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
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

