package com.fernanda.medialert.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmUtils {

    // Programa alarma a la hora especificada (formato "1:30 PM" o "13:30")
    fun programarAlarma(
        context: Context,
        idProgramacion: Int,
        nombre: String,
        dosis: String,
        hora: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = crearIntent(context, idProgramacion, nombre, dosis)
        val pendingIntent = PendingIntent.getBroadcast(
            context, idProgramacion, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val partes = hora.trim().split(Regex("[:\\s]+"))
            var h = partes.getOrElse(0) { "0" }.toInt()
            val m = partes.getOrElse(1) { "0" }.toInt()
            val ampm = partes.getOrElse(2) { "" }.uppercase()
            if (ampm == "PM" && h < 12) h += 12
            if (ampm == "AM" && h == 12) h = 0
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    // Cancela la alarma de un medicamento (cuando el usuario OMITE o TOMA desde la app)
    // Así no suena la notificación de una toma que ya fue registrada
    fun cancelarAlarma(context: Context, idProgramacion: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, idProgramacion, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    // Posponer: reprograma la alarma en 5 minutos
    fun posponerAlarma(
        context: Context,
        idProgramacion: Int,
        nombre: String,
        dosis: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = crearIntent(context, idProgramacion, nombre, dosis)
        val pendingIntent = PendingIntent.getBroadcast(
            context, idProgramacion, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val tiempo = System.currentTimeMillis() + (5 * 60 * 1000L)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempo, pendingIntent)
    }

    private fun crearIntent(
        context: Context,
        idProgramacion: Int,
        nombre: String,
        dosis: String
    ) = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("ID_PROGRAMACION", idProgramacion)
        putExtra("NOMBRE_MEDICAMENTO", nombre)
        putExtra("DOSIS", dosis)
    }
}

