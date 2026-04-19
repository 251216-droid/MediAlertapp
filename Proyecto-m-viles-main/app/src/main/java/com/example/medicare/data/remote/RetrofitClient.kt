package com.example.medicare.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Tu nueva IP según el ipconfig es: 192.168.1.70
    private const val BASE_URL = "http://192.168.1.70:3000/" 

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio para Autenticación (Login/Registro)
    val authService: AuthApiService by lazy {
        instance.create(AuthApiService::class.java)
    }

    // Servicio para Enfermedades
    val enfermedadService: EnfermedadApiService by lazy {
        instance.create(EnfermedadApiService::class.java)
    }

    // Servicio para Medicamentos y Programación
    val medicamentoService: MedicamentoApiService by lazy {
        instance.create(MedicamentoApiService::class.java)
    }

    // Servicio para Historial y Próximas Dosis
    val historialService: HistorialApiService by lazy {
        instance.create(HistorialApiService::class.java)
    }
}
