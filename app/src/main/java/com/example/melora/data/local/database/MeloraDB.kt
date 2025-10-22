package com.example.melora.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.upload.UploadDao
import com.example.melora.data.local.upload.UploadEntity
import com.example.melora.data.local.users.UserDao
import com.example.melora.data.local.users.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date

// @Database registra entidades y versión del esquema.
// version = 1: primera versión del esquema local.
@Database(
    entities = [
        SongEntity::class,
        UploadEntity::class,
        UserEntity::class
    ],
    version = 6,
    exportSchema = true // Mantener true para inspeccionar el esquema (útil en educación)
)
abstract class MeloraDB : RoomDatabase() {

    // Exponemos los DAO de canciones y subidas
    abstract fun songDao(): SongDao
    abstract fun uploadDao(): UploadDao
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var INSTANCE: MeloraDB? = null              // Instancia singleton
        private const val DB_NAME = "melora.db"                // Nombre del archivo .db

        // Obtiene la instancia única de la base de datos
        fun getInstance(context: Context): MeloraDB {
            return INSTANCE ?: synchronized(this) {
                // Construimos la DB con callback de precarga
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeloraDB::class.java,
                    DB_NAME
                )
                    // Callback que se ejecuta al crear la DB por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Corrutina en hilo IO para precargar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val songDao = getInstance(context).songDao()

                                val userDao = getInstance(context).userDao()

                                // Precarga de canciones de ejemplo
                                val seedSongs = listOf(
                                    SongEntity(
                                        songName = "Canción de Ejemplo 1",
                                        songDescription = "Canción de Ejemplo 1",
                                        songPath = "https://example.com/song1.mp3",
                                        coverArt = "https://example.com/song1.mp3",
                                        durationSong = 180,
                                        creationDate = System.currentTimeMillis()
                                    ),
                                    SongEntity(
                                        songName = "Canción de Ejemplo 2",
                                        songDescription = "Canción de Ejemplo 2",
                                        songPath = "https://example.com/song2.mp3",
                                        coverArt =  "https://example.com/song2.mp3",
                                        durationSong = 210,
                                        creationDate = System.currentTimeMillis())
                                    )

                                // Inserta canciones sólo si la tabla está vacía
                                if (songDao.getAllSong().isEmpty()) {
                                    seedSongs.forEach { songDao.insert(it) }
                                }
                            }
                        }
                    })
                    // Si cambias la versión sin migraciones, destruye y recrea (modo educativo)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
    }
}
