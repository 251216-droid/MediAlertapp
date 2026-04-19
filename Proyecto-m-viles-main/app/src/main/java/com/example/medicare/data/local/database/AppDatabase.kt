package com.example.medicare.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medicare.data.local.entity.Usuario
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.data.local.entity.Programacion
import com.example.medicare.data.local.entity.HistorialToma
import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.dao.EnfermedadDao
import com.example.medicare.data.local.dao.MedicamentoDao
import com.example.medicare.data.local.dao.ProgramacionDao
import com.example.medicare.data.local.dao.HistorialTomaDao

@Database(
    entities = [
        Usuario::class,
        Enfermedad::class,
        Medicamento::class,
        Programacion::class,
        HistorialToma::class
    ],
    version = 2,
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
