package com.example.medicare

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.medicare.data.local.database.AppDatabase
import com.example.medicare.data.remote.RetrofitClient
import com.example.medicare.data.repositories.*

class MedicareApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val enfermedadRepository by lazy {
        EnfermedadRepository(database.enfermedadDao(), RetrofitClient.enfermedadService)
    }

    val usuarioRepository by lazy {
        UsuarioRepository(database.usuarioDao(), RetrofitClient.authService)
    }

    val medicamentoRepository by lazy {
        MedicamentoRepository(this, database.medicamentoDao(), database.programacionDao(), RetrofitClient.medicamentoService)
    }

    val historialRepository by lazy {
        HistorialRepository(RetrofitClient.historialService)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de MediAlert"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("medicare_reminders", name, importance).apply {
                description = "Canal para las alarmas de medicamentos"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
