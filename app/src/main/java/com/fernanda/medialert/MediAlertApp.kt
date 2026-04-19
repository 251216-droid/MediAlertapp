package com.fernanda.medialert

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.fernanda.medialert.data.local.database.AppDatabase
import com.fernanda.medialert.data.remote.RetrofitClient
import com.fernanda.medialert.data.repositories.*

class MediAlertApp : Application() {
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
            val channel = NotificationChannel("medialert_reminders", name, importance).apply {
                description = "Canal para las alarmas de medicamentos"
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(soundUri, audioAttr)
                enableVibration(true)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}



