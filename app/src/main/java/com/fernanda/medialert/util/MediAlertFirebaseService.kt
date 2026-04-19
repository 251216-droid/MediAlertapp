package com.fernanda.medialert.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fernanda.medialert.MediAlertApp
import com.fernanda.medialert.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediAlertFirebaseService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MediAlertFCM"
        const val CHANNEL_ID = "medialert_reminders"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo FCM Token recibido")
        enviarTokenAlServidor(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "=== FCM RECIBIDO ===")
        Log.d("FCM_MSG", "message: $message")

        Log.d(TAG, "from: ${message.from}")
        Log.d(TAG, "data: ${message.data}")

        val data           = message.data
        val idProgramacion = data["idProgramacion"]?.toIntOrNull() ?: -1
        val nombre         = data["nombre"] ?: "Medicamento"
        val dosis          = data["dosis"] ?: ""
        val fechaProgramadaDt = data["fecha_programada_dt"] ?: ""

        if (idProgramacion == -1) {
            Log.w(TAG, "idProgramacion inválido, ignorando mensaje")
            return
        }

        Log.d(TAG, "Mostrando notificación: idProg=$idProgramacion nombre=$nombre")
        mostrarNotificacion(idProgramacion, nombre, dosis, fechaProgramadaDt)
    }


    private fun mostrarNotificacion(idProgramacion: Int, nombre: String, dosis: String, fechaProgramadaDt: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de alta importancia
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Eliminar canal viejo si existe (para aplicar nueva configuración)
            val canalExistente = nm.getNotificationChannel(CHANNEL_ID)
            if (canalExistente == null) {
                val canal = NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios MediAlert",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alertas de toma de medicamentos"
                    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val audioAttr = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    setSound(soundUri, audioAttr)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                }
                nm.createNotificationChannel(canal)
                Log.d(TAG, "Canal de notificación creado")
            }
        }

        val intentTomado = Intent().apply {
            action    = "ACTION_TOMADO"
            component = ComponentName(
                packageName,
                "com.fernanda.medialert.util.ReminderReceiver"
            )
            putExtra("ID_PROGRAMACION",   idProgramacion)
            putExtra("NOMBRE_MEDICAMENTO", nombre)
            putExtra("DOSIS",              dosis)
            putExtra("FECHA_PROGRAMADA_DT", fechaProgramadaDt)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        val pTomado = PendingIntent.getBroadcast(
            this,
            idProgramacion + 1000,
            intentTomado,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentPosponer = Intent().apply {
            action    = "ACTION_POSPONER"
            component = ComponentName(
                packageName,
                "com.fernanda.medialert.util.ReminderReceiver"
            )
            putExtra("ID_PROGRAMACION",   idProgramacion)
            putExtra("NOMBRE_MEDICAMENTO", nombre)
            putExtra("DOSIS",              dosis)
            putExtra("FECHA_PROGRAMADA_DT", fechaProgramadaDt)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        val pPosponer = PendingIntent.getBroadcast(
            this,
            idProgramacion + 2000,
            intentPosponer,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_medialert)
            .setContentTitle(" ¡Hora de tu medicina!")
            .setContentText("$nombre · $dosis")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Es hora de tomar:\n$nombre\nDosis: $dosis")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)   // MAX para asegurar visibilidad
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Categoría ALARM pasa Doze mode
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Visible en pantalla bloqueada
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .addAction(R.drawable.exitoso,    " TOMADO",   pTomado)
            .addAction(R.drawable.no_exitoso, " POSPONER", pPosponer)
            .setAutoCancel(false)
            .setOngoing(false)
            .build()

        nm.notify(idProgramacion, notificacion)
        Log.d(TAG, "Notificación mostrada con ID=$idProgramacion")
    }

    // ── Guardar token en la API ───────────────────────────────────────────────
    private fun enviarTokenAlServidor(token: String) {
        val prefs     = getSharedPreferences("medialert_prefs", Context.MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)

        if (idUsuario == -1) {
            prefs.edit().putString("fcm_token_pendiente", token).apply()
            Log.d(TAG, "Token guardado localmente (sin sesión activa)")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = applicationContext as MediAlertApp
                app.usuarioRepository.actualizarFcmToken(idUsuario, token)
                Log.d(TAG, "Token FCM actualizado en servidor para usuario $idUsuario")
            } catch (e: Exception) {
                Log.e(TAG, "Error enviando token: ${e.message}")
            }
        }
    }
}
