package com.fernanda.medialert.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fernanda.medialert.MediAlertApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    companion object { private const val TAG = "ReminderReceiver" }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val idProg = intent.getIntExtra("ID_PROGRAMACION", -1)
        val nombre = intent.getStringExtra("NOMBRE_MEDICAMENTO") ?: "Medicamento"
        val dosis  = intent.getStringExtra("DOSIS") ?: ""

        Log.d(TAG, "onReceive → action=$action idProg=$idProg nombre=$nombre")

        when (action) {

            // ── TOMADO ────────────────────────────────────────────────────────
            // Guarda "Tomado" en historial → cancela notificación
            // La API detecta "Tomado" → no vuelve a notificar hasta la siguiente hora
            "ACTION_TOMADO" -> {
                Log.d(TAG, "✅ TOMADO: prog=$idProg")
                cancelarNotificacion(context, idProg)
                registrarEnServidor(context, idProg, nombre, "Tomado")
            }

            // ── POSPONER ──────────────────────────────────────────────────────
            // Guarda "Pospuesto" en historial → cancela notificación
            // La API detecta "Pospuesto" → reenvía FCM en ~5 minutos
            "ACTION_POSPONER" -> {
                Log.d(TAG, "⏰ POSPONER: prog=$idProg")
                cancelarNotificacion(context, idProg)
                registrarEnServidor(context, idProg, nombre, "Pospuesto")
            }

            else -> Log.w(TAG, "Acción no reconocida: $action")
        }
    }

    private fun registrarEnServidor(context: Context, idProg: Int, nombre: String, estado: String) {
        if (idProg == -1) {
            Log.e(TAG, "idProg inválido, no se puede registrar")
            return
        }
        val app = context.applicationContext as MediAlertApp
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.historialRepository.registrarToma(idProg, nombre, estado)
                Log.d(TAG, "✅ Historial guardado: estado=$estado prog=$idProg")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error guardando historial: ${e.message}")
            }
        }
    }

    private fun cancelarNotificacion(context: Context, idProg: Int) {
        if (idProg == -1) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(idProg)
        Log.d(TAG, "Notificación cancelada: id=$idProg")
    }
}
