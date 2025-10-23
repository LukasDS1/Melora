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
    version = 7,
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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Corrutina en hilo IO para precargar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {

                                val instance = getInstance(context)
                                val songDao = instance.songDao()
                                val userDao = instance.userDao()
                                val uploadDao = instance.uploadDao()

                                val seedUsers = listOf(
                                    UserEntity(
                                        email = "user1@email.com",
                                        nickname = "UserOne",
                                        pass = "PASSUSER1",
                                        idUser = 1,
                                    ),
                                    UserEntity(
                                        email = "user2@email.com",
                                        nickname = "UserTwo",
                                        pass = "PASSUSER2",
                                        idUser = 2
                                    ),
                                    UserEntity(
                                        email = "user3@email.com",
                                        nickname = "UserThree",
                                        pass = "PASSUSER3",
                                        idUser = 3
                                    )
                                )

                                if (userDao.getAllUser().isEmpty()) {
                                    seedUsers.forEach { userDao.upsertUser(it) }
                                }

                                val userIds = mutableListOf<Long>()
                                seedUsers.forEach { user ->
                                    val id = userDao.upsertUser(user)
                                    userIds.add(id)
                                }


                                // Precarga de canciones de ejemplo
                                val seedSongs = listOf(
                                    SongEntity(
                                        songName = "Cielo Azul",
                                        songDescription = "Canción tranquila de ejemplo",
                                        songPath = "/example.com/song1.mp3",
                                        coverArt = "/example.com/song1cover.png",
                                        durationSong = 180,
                                        creationDate = System.currentTimeMillis()
                                    ),
                                    SongEntity(
                                        songName = "Lluvia de Verano",
                                        songDescription = "Canción con ritmo alegre",
                                        songPath = "/example.com/song2.mp3",
                                        coverArt = "/example.com/song2cover.png",
                                        durationSong = 210,
                                        creationDate = System.currentTimeMillis()
                                    ),
                                    SongEntity(
                                        songName = "Noches Lentas",
                                        songDescription = "Canción suave y relajante",
                                        songPath = "/example.com/song3.mp3",
                                        coverArt = "/example.com/song3cover.png",
                                        durationSong = 240,
                                        creationDate = System.currentTimeMillis()
                                    )
                                )

                                // Inserta canciones sólo si la tabla está vacía
                                if (songDao.getAllSong().isEmpty()) {
                                    seedSongs.forEach { songDao.insert(it) }
                                }

                                val songIds = mutableListOf<Long>()
                                seedSongs.forEach { song ->
                                    val id = songDao.insert(song)
                                    songIds.add(id)
                                }

                                val seedUploads = listOf(
                                    UploadEntity(
                                        userId = userIds[0],
                                        idSong = songIds[0],
                                        stateId = 1
                                    ),
                                    UploadEntity(
                                        userId = userIds[1],
                                        idSong = songIds[1],
                                        stateId = 1
                                    ),
                                    UploadEntity(
                                        userId = userIds[2],
                                        idSong = songIds[2],
                                        stateId = 1
                                    )
                                )

                                seedUploads.forEach { uploadDao.insert(it) }
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
