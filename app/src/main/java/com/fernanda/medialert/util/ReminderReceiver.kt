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

    companion object {
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val idProg = intent.getIntExtra("ID_PROGRAMACION", -1)
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", idProg)
        val nombre = intent.getStringExtra("NOMBRE_MEDICAMENTO") ?: "Medicamento"
        val fechaProgramadaDt = intent.getStringExtra("FECHA_PROGRAMADA_DT")

        Log.d(TAG, "onReceive action=$action idProg=$idProg notificationId=$notificationId nombre=$nombre")

        when (action) {
            "ACTION_TOMADO" -> {
                Log.d(TAG, "TOMADO: prog=$idProg")
                cancelarNotificacion(context, notificationId)
                registrarEnServidor(context, idProg, fechaProgramadaDt, "Tomado")
            }

            "ACTION_POSPONER" -> {
                Log.d(TAG, "POSPONER: prog=$idProg")
                cancelarNotificacion(context, notificationId)
                registrarEnServidor(context, idProg, fechaProgramadaDt, "Pospuesto")
            }

            else -> Log.w(TAG, "Accion no reconocida: $action")
        }
    }

    private fun registrarEnServidor(
        context: Context,
        idProg: Int,
        fechaProgramadaDt: String?,
        estado: String
    ) {
        if (idProg == -1) {
            Log.e(TAG, "idProg invalido, no se puede registrar")
            return
        }

        val app = context.applicationContext as MediAlertApp
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.historialRepository.registrarToma(
                    idProg,
                    "",
                    fechaProgramadaDt,
                    estado
                )
                Log.d(TAG, "Historial guardado: estado=$estado prog=$idProg")
            } catch (e: Exception) {
                Log.e(TAG, "Error guardando historial: ${e.message}")
            }
        }
    }

    private fun cancelarNotificacion(context: Context, notificationId: Int) {
        if (notificationId == -1) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationId)
        Log.d(TAG, "Notificacion cancelada: id=$notificationId")
    }
}
