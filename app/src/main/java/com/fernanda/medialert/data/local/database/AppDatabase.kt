package com.fernanda.medialert.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fernanda.medialert.data.local.entity.Usuario
import com.fernanda.medialert.data.local.entity.Enfermedad
import com.fernanda.medialert.data.local.entity.Medicamento
import com.fernanda.medialert.data.local.entity.Programacion
import com.fernanda.medialert.data.local.entity.HistorialToma
import com.fernanda.medialert.data.local.dao.UsuarioDao
import com.fernanda.medialert.data.local.dao.EnfermedadDao
import com.fernanda.medialert.data.local.dao.MedicamentoDao
import com.fernanda.medialert.data.local.dao.ProgramacionDao
import com.fernanda.medialert.data.local.dao.HistorialTomaDao

@Database(
    entities = [
        Usuario::class,
        Enfermedad::class,
        Medicamento::class,
        Programacion::class,
        HistorialToma::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun enfermedadDao(): EnfermedadDao
    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun programacionDao(): ProgramacionDao
    abstract fun historialTomaDao(): HistorialTomaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medialert_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

