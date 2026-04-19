package com.example.medicare.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmUtils {
    fun programarAlarma(context: Context, idProgramacion: Int, nombre: String, dosis: String, hora: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("NOMBRE_MEDICAMENTO", nombre)
            putExtra("DOSIS", dosis)
            putExtra("ID_PROGRAMACION", idProgramacion)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, idProgramacion, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val partes = hora.split(":", " ")
            var h = partes[0].toInt()
            val m = partes[1].toInt()
            if (partes.size > 2 && partes[2].equals("PM", ignoreCase = true) && h < 12) h += 12
            if (partes.size > 2 && partes[2].equals("AM", ignoreCase = true) && h == 12) h = 0
            
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    // FASE 3: Lógica de Posponer
    fun posponerAlarma(context: Context, idProgramacion: Int, nombre: String, dosis: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("NOMBRE_MEDICAMENTO", nombre)
            putExtra("DOSIS", dosis)
            putExtra("ID_PROGRAMACION", idProgramacion)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, idProgramacion, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Reprogramar para 10 minutos después
        val tiempo = System.currentTimeMillis() + (10 * 60 * 1000)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempo, pendingIntent)
    }
}
