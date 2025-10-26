package com.example.melora.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.melora.data.local.estado.EstadoDao
import com.example.melora.data.local.estado.EstadoEntity
import com.example.melora.data.local.favorites.FavoriteDao
import com.example.melora.data.local.favorites.FavoriteEntity
import com.example.melora.data.local.rol.RolDao
import com.example.melora.data.local.rol.RolEntity
import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.upload.UploadDao
import com.example.melora.data.local.upload.UploadEntity
import com.example.melora.data.local.users.UserDao
import com.example.melora.data.local.users.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.sql.Date

// @Database registra entidades y versión del esquema.
// version = 1: primera versión del esquema local.
@Database(
    entities = [
        SongEntity::class,
        UploadEntity::class,
        UserEntity::class,
        FavoriteEntity::class,
        RolEntity::class,
        EstadoEntity::class
    ],
    version = 17,
    exportSchema = true // Mantener true para inspeccionar el esquema (útil en educación)
)
abstract class MeloraDB : RoomDatabase() {

    // Exponemos los DAO de canciones y subidas
    abstract fun rolDao() : RolDao
    abstract fun songDao(): SongDao
    abstract fun uploadDao(): UploadDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun userDao(): UserDao

    abstract fun estadoDao(): EstadoDao
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
                        }

                        // Waits until the scheme is created to load info
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.preloadData(context)
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

    suspend fun preloadData(context:Context) {
        val rolDao = rolDao()
        val estadoDao = estadoDao()
        val userDao = userDao()
        val songDao = songDao()
        val uploadDao = uploadDao()

        val seedEstado = listOf(
            EstadoEntity(
                nameEstado = "Activo"
            ),
            EstadoEntity(
                nameEstado = "Inactivo"
            )
        )

        if(estadoDao.getAllEstado().isEmpty()){
            seedEstado.forEach { estadoDao.insertEstado(it)}
        }

        val seedRol = listOf(
            RolEntity(
                rolName = "Admin"
            ),
            RolEntity(
                rolName = "User"
            )
        )

        if(rolDao.getAllRol().isEmpty()){
            seedRol.forEach { rolDao.insert(it) }
        }

        fun copyAssetToInternal(assetPath: String, destDirName: String): String {
            val destDir = File(context.filesDir, destDirName).apply { mkdirs() }
            val fileName = File(assetPath).name
            val destFile = File(destDir, fileName)

            if (!destFile.exists()) {
                context.assets.open(assetPath).use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            return destFile.absolutePath
        }

        val seedUsers = listOf(
            UserEntity(1,email = "user1@email.com", nickname = "IndiAladinOficial", pass = "Passuser1", rolId = 1, estadoId = 1),
            UserEntity(2, email = "user2@email.com", nickname = "Hudson Mohawke", pass = "Passuser2", rolId = 2, estadoId = 1),
            UserEntity(3, email = "user3@email.com", nickname = "terrariaLord", pass = "Passuser3", rolId = 2, estadoId = 1)
        )

        if (userDao.getAllUser().isEmpty()) {
            seedUsers.forEach { userDao.upsertUser(it) }
        }

        val seedSongs = listOf(
            SongEntity(1,
                songName = "aladin",
                songDescription = "aladin",
                songPath = copyAssetToInternal("songs/aladin.mp3","songs"),
                coverArt = copyAssetToInternal("covers/portada1.png","covers"),
                durationSong = 180,
                creationDate = System.currentTimeMillis()
            ),
            SongEntity(2,
                songName = "cbat",
                songDescription = "rusky",
                songPath = copyAssetToInternal("songs/cbat.mp3","songs"),
                coverArt = copyAssetToInternal("covers/cbat.png","covers"),
                durationSong = 210,
                creationDate = System.currentTimeMillis()
            ),
            SongEntity(3,
                songName = "Terraria song by me",
                songDescription = "punshis punshis",
                songPath = copyAssetToInternal("songs/terraria.mp3","songs"),
                coverArt = copyAssetToInternal("covers/terrariaport.png","covers"),
                durationSong = 240,
                creationDate = System.currentTimeMillis()
            )
        )
        if (songDao.countSongs() == 0) {
            seedSongs.forEach { songDao.insert(it) }
        }

        val seedUploads = listOf(
            UploadEntity(
                userId = 1,
                idSong = 1,
                stateId = 1
            ),
            UploadEntity(
                userId = 2,
                idSong = 2,
                stateId = 1
            ),
            UploadEntity(
                userId = 3,
                idSong = 3,
                stateId = 1
            )
        )
        if(uploadDao.getAllUpload().isEmpty()){
            seedUploads.forEach { uploadDao.insert(it) }
        }

    }
}
