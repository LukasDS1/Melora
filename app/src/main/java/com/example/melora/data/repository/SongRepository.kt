package com.example.melora.data.repository
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongEntity
import java.sql.Date

class SongRepository(
    private val songDao: SongDao,
    private val context: Context
) {
    suspend fun uploadMusic(songName: String,songPath: Uri?,coverArt:Uri?,songDescription:String?,durationSong:Int,creationDate: Date): Result<Long> {
        try {
            if (songName.isBlank() || songPath == null) {
                return Result.failure(IllegalArgumentException("Name or Song file are invalid"))
            }
            val actualDuration = getAudioDuration(songPath)
            // con el let el bloque se ejecuta solo si no es nulo
            val convertToByte = coverArt?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use {it.readBytes()}  //utilizamos use para cerrar el imput
            } ?: ByteArray(0)
            val songEntity = SongEntity(
                songName = songName,
                songDescription = songDescription,
                songPath = songPath.toString(), // transformamos el la uri como string
                coverArt = convertToByte,
                durationSong = actualDuration,
                creationDate = Date(System.currentTimeMillis())
            )
            val id = songDao.insert(songEntity)
            return Result.success(id)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun getAudioDuration(songUri: Uri): Int {
        return try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(context, songUri)
                prepare()
            }
            val duration = mediaPlayer.duration / 1000
            mediaPlayer.release()
            duration
        } catch (e: Exception) {
            0
        }
    }

    suspend fun searchSong(query:String): Result<List<SongEntity>>{
        try {
           val songs = if(query.isBlank()){
                emptyList()  //songDao.getAllSong() revisar
           } else{
               songDao.searchSongs(query)
           }
          return Result.success(songs)
       }catch (e: Exception){
          return Result.failure(e)
       }
    }

    suspend fun getAllSongs(): List<SongEntity> = songDao.getAllSong()
}
