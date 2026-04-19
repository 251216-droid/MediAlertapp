package com.example.medicare.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medicare.MedicareApp
import com.example.medicare.MainActivity
import com.example.medicare.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val idProgramacion = intent.getIntExtra("ID_PROGRAMACION", -1)
        val nombre = intent.getStringExtra("NOMBRE_MEDICAMENTO") ?: "Medicamento"
        val dosis = intent.getStringExtra("DOSIS") ?: ""

        // 1. Manejar reinicio del celular (Reprogramar todo)
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            reprogramarTodasLasAlarmas(context)
            return
        }

        // 2. Manejar botón "TOMADO" desde la notificación
        if (action == "ACTION_TOMADO") {
            registrarTomaEnServidor(context, idProgramacion)
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(idProgramacion)
            return
        }

        // 3. Manejar botón "POSPONER"
        if (action == "ACTION_POSPONER") {
            AlarmUtils.posponerAlarma(context, idProgramacion, nombre, dosis)
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(idProgramacion)
            return
        }

        // 4. Mostrar la notificación normal si no es una acción de botón
        showNotification(context, nombre, dosis, idProgramacion)
    }

    private fun registrarTomaEnServidor(context: Context, idProg: Int) {
        val app = context.applicationContext as MedicareApp
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.historialRepository.registrarToma(idProg, "Notificación", "Tomado")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun reprogramarTodasLasAlarmas(context: Context) {
        val app = context.applicationContext as MedicareApp
        CoroutineScope(Dispatchers.IO).launch {
            val medicamentos = app.medicamentoRepository.obtenerMedicamentos(1) // Ejemplo para id 1
            // Aquí se recorrerían los medicamentos y se llamarían a AlarmUtils
        }
    }

    private fun showNotification(context: Context, nombre: String, dosis: String, idProg: Int) {
        val channelId = "medicare_reminders"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        val intentTomado = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_TOMADO"
            putExtra("ID_PROGRAMACION", idProg)
        }
        val pTomado = PendingIntent.getBroadcast(context, idProg + 1, intentTomado, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val intentPosponer = Intent(context, ReminderReceiver::class.java).apply {
            action = "ACTION_POSPONER"
            putExtra("ID_PROGRAMACION", idProg)
            putExtra("NOMBRE_MEDICAMENTO", nombre)
            putExtra("DOSIS", dosis)
        }
        val pPosponer = PendingIntent.getBroadcast(context, idProg + 2, intentPosponer, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_medicare)
            .setContentTitle("¡Hora de tu medicina!")
            .setContentText("Te toca tomar: $nombre ($dosis)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.exitoso, "TOMADO", pTomado)
            .addAction(R.drawable.no_exitoso, "POSPONER", pPosponer)
            .setAutoCancel(true)
            .build()

        nm.notify(idProg, notification)
    }
}
