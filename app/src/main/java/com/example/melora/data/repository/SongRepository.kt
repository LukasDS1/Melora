package com.example.melora.data.repository

import android.net.Uri
import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.users.UserEntity
import java.util.Date


class SongRepository(
    private val songDao: SongDao
) {
    suspend fun uploadMusic(songName: String,songPath: Uri?,coverArt:Uri?,songDescription:String?,durationSong:Int,creationDate: Long): Result<Long> {
        try {
            if (songName.isBlank() || songPath == null) {
                return Result.failure(IllegalArgumentException("Name or Song file are invalid"))
            }
            //
            val songEntity = SongEntity(
                songName = songName,
                songDescription = songDescription,
                songPath = songPath.toString(), // transformamos el la uri como string
                durationSong = 1,
                coverArt = coverArt?.toString(),
                creationDate = Date().time
            )
            val id = songDao.insert(songEntity)
            return Result.success(id)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun deleteSong(songId: Long): Result<Unit> {
        return try {
            songDao.deleteById(songId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun playSongByID(songId:Long): SongDetailed{
        return songDao.getSongByID(songId)
    }

    suspend fun getSong(query: String): Result<List<SongDetailed>> {
        return try {
            val detailedSongs = songDao.getSong(query)
            Result.success(detailedSongs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSongs(): List<SongDetailed> = songDao.getAllSong()

    suspend fun  getSongsForArtist(id:Long): Result<List<SongDetailed>>{
        return try {
            val detailedSong = songDao.getSongsForArtist(id)
            Result.success(detailedSong)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getArtistById(artistId: Long): UserEntity? {
        return songDao.getUserById(artistId)
    }
}
